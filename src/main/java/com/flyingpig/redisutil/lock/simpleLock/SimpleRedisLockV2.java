package com.flyingpig.redisutil.lock.simpleLock;

import com.flyingpig.redisutil.lock.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
    第二版：加入UUID与原来的线程ID共同作为线程标识，
    让不同线程取得相同锁的概率大大降低，防止并发情况下锁的误删行为。
 */
public class SimpleRedisLockV2 implements Lock {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    // 线程标识
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

    @Override
    public void unlock(String lockKey) {
        // 获取线程标示
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        // 获取锁中的标示
        String id = stringRedisTemplate.opsForValue().get(lockKey);
        // 判断标示是否一致
        if(threadId.equals(id)) {
            // 释放锁
            stringRedisTemplate.delete(lockKey);
        }
    }

}