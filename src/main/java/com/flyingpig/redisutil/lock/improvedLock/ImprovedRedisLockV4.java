package com.flyingpig.redisutil.lock.improvedLock;

import com.flyingpig.redisutil.lock.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/*
    增加看门狗实现自动续期的功能
 */
@Component
public class ImprovedRedisLockV4 implements Lock {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String ID_PREFIX = UUID.randomUUID() + "-";

    private static final DefaultRedisScript<Long> tryLockScript = new DefaultRedisScript<>();
    private static final DefaultRedisScript<Long> unlockScript = new DefaultRedisScript<>();

    private static final long RETRY_INTERVAL_MS = 100;
    private static final int MAX_RETRY_TIMES = 3;

    // 定时任务执行间隔（秒）
    private static final long WATCHDOG_INTERVAL_SEC = 10;

    // 共享的 ScheduledExecutorService
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 保存锁key到其watchdog定时任务的映射
    private final Map<String, ScheduledFuture<?>> watchdogTasks = new ConcurrentHashMap<>();

    // 保存线程id到锁的重入计数
    private final Map<String, Integer> lockCounts = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化 Lua 脚本
        try {
            String tryLockScriptContent = new String(Files.readAllBytes(Paths.get(new ClassPathResource("scripts/reentrant-lock/tryLock.lua").getURI())));
            tryLockScript.setScriptText(tryLockScriptContent);
            tryLockScript.setResultType(Long.class);

            String unlockScriptContent = new String(Files.readAllBytes(Paths.get(new ClassPathResource("scripts/reentrant-lock/unLock.lua").getURI())));
            unlockScript.setScriptText(unlockScriptContent);
            unlockScript.setResultType(Long.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean tryLock(String lockKey, long timeoutSec) {
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        long end = System.currentTimeMillis() + timeoutSec * 1000;
        int retries = 0;
        while (true) {
            Long result = stringRedisTemplate.execute(tryLockScript, Collections.singletonList(lockKey), String.valueOf(timeoutSec * 1000), threadId);
            if (result != null && result == 1) {
                lockCounts.merge(threadId, 1, Integer::sum);
                startWatchdog(lockKey, timeoutSec); // 启动看门狗
                return true;
            }
            if (System.currentTimeMillis() >= end || retries >= MAX_RETRY_TIMES) {
                return false;
            }
            retries++;
            try {
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    @Override
    public void unlock(String lockKey) {
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        lockCounts.computeIfPresent(threadId, (key, count) -> {
            if (count == 1) {
                stringRedisTemplate.execute(unlockScript, Collections.singletonList(lockKey), threadId);
                stopWatchdog(lockKey); // 停止看门狗
                return null; // 删除这个线程的记录
            }
            return count - 1;
        });
    }

    // 启动看门狗
    private void startWatchdog(String lockKey, long timeoutSec) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            String threadId = ID_PREFIX + Thread.currentThread().getId();
            Long result = stringRedisTemplate.execute(tryLockScript, Collections.singletonList(lockKey), String.valueOf(timeoutSec * 1000), threadId);
            if (result == null || result != 1) {
                stopWatchdog(lockKey); // 停止看门狗
            }
        }, WATCHDOG_INTERVAL_SEC, WATCHDOG_INTERVAL_SEC, TimeUnit.SECONDS);
        watchdogTasks.put(lockKey, future);
    }

    // 停止看门狗
    private void stopWatchdog(String lockKey) {
        ScheduledFuture<?> future = watchdogTasks.remove(lockKey);
        if (future != null) {
            future.cancel(true);
        }
    }
}

