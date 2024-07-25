package com.flyingpig.redisutil.lock.simpleLock;

import com.flyingpig.redisutil.lock.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/*
    第一版：采用setx命令加锁并设置过期时间，
    在并发情况下存在锁误删问题
 */
public class SimpleRedisLockV1 implements Lock {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean tryLock(String lockKey, long timeoutSec) {
        // 获取线程标识
        String threadId = Thread.currentThread().getId()+"";
        // 获取锁并判断是否成功
        // setIfAbsent方法是原子的，只有一个线程能够获取到锁，对应redis的setNx命令
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(lockKey, threadId, timeoutSec, TimeUnit.SECONDS));
    }

    @Override
    public void unlock(String lockKey) {
        //通过del删除锁
        stringRedisTemplate.delete(lockKey);
    }
}
