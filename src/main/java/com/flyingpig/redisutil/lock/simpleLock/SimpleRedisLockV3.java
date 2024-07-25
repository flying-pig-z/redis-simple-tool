package com.flyingpig.redisutil.lock.simpleLock;

import com.flyingpig.redisutil.lock.Lock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
    第三版：Lua脚本优化。
    增加判断和删除锁的操作的原子性，防止极端情况下的误删问题
 */

public class SimpleRedisLockV3 implements Lock {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    private static final String ID_PREFIX = UUID.randomUUID() + "-";

    @Override
    public boolean tryLock(String lockKey, long timeoutSec) {
        // 获取线程标示
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        // 获取锁并判断是否成功
        // setIfAbsent方法是原子的，只有一个线程能够获取到锁，对应redis的setNx命令
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(lockKey, threadId, timeoutSec, TimeUnit.SECONDS));
    }

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("/scripts/simple-lock/unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public void unlock(String lockKey) {
        // 调用lua脚本
        stringRedisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(lockKey),
                ID_PREFIX + Thread.currentThread().getId());
    }


}
