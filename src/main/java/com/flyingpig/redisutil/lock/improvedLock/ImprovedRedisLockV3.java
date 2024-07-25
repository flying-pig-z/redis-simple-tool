package com.flyingpig.redisutil.lock.improvedLock;


import com.flyingpig.redisutil.lock.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.UUID;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import java.util.Collections;
import org.springframework.core.io.ClassPathResource;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
/*
    增加锁重试的功能
 */
@Component
public class ImprovedRedisLockV3 implements Lock {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String ID_PREFIX = UUID.randomUUID() + "-";

    private static final DefaultRedisScript<Long> tryLockScript = new DefaultRedisScript<>();
    private static final DefaultRedisScript<Long> unlockScript = new DefaultRedisScript<>();

    // 锁的最大重试次数
    private static final int MAX_RETRY_TIMES = 3;

    // 锁的重试间隔时间（毫秒）
    private static final long RETRY_INTERVAL_MS = 100;

    @PostConstruct
    public void init() throws IOException {
        // 读取 tryLock.lua 脚本内容
        String tryLockScriptContent = new String(Files.readAllBytes(Paths.get(new ClassPathResource("scripts/reentrant-lock/tryLock.lua").getURI())));
        tryLockScript.setScriptText(tryLockScriptContent);
        tryLockScript.setResultType(Long.class);

        // 读取 unlock.lua 脚本内容
        String unlockScriptContent = new String(Files.readAllBytes(Paths.get(new ClassPathResource("scripts/reentrant-lock/unLock.lua").getURI())));
        unlockScript.setScriptText(unlockScriptContent);
        unlockScript.setResultType(Long.class);
    }

    @Override
    public boolean tryLock(String lockKey, long timeoutSec) {
        // 生成唯一的线程标识
        String threadId = ID_PREFIX + Thread.currentThread().getId();

        // 计算超时的绝对时间
        long end = System.currentTimeMillis() + timeoutSec * 1000;

        int retries = 0;
        while (true) {
            // 执行 tryLock.lua 脚本尝试获取锁
            Long result = stringRedisTemplate.execute(
                    tryLockScript,  // 使用定义的 Lua 脚本
                    Collections.singletonList(lockKey),  // 锁的名称作为 KEYS[1]
                    String.valueOf(timeoutSec * 1000),  // 锁的过期时间（毫秒）作为 ARGV[1]
                    threadId  // 线程标识作为 ARGV[2]
            );

            // 如果脚本返回 1，表示成功获取锁；返回 0 表示获取锁失败
            if (result != null && result == 1) {
                return true;  // 成功获取锁，返回 true
            }

            // 未获取到锁，判断是否超时或者达到最大重试次数
            if (System.currentTimeMillis() >= end || retries >= MAX_RETRY_TIMES) {
                return false;  // 超时或者达到最大重试次数，返回 false
            }

            retries++;

            // 等待一段时间后进行重试
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
        // 生成唯一的线程标识
        String threadId = ID_PREFIX + Thread.currentThread().getId();

        // 执行 unlock.lua 脚本释放锁
        stringRedisTemplate.execute(
                unlockScript,  // 使用定义的 Lua 脚本
                Collections.singletonList(lockKey),  // 锁的名称作为 KEYS[1]
                threadId  // 线程标识作为 ARGV[1]
        );
    }
}

