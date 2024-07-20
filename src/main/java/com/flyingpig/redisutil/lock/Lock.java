package com.flyingpig.redisutil.lock;

public interface Lock {
    /**
     * 尝试获取锁
     * @param lockKey 锁的key
     * @param  timeoutSec 获取锁的超时时间，过期后自动释放
     * @return 是否获取成功
     */
    boolean tryLock(String lockKey, long timeoutSec);

    /**
     * 释放锁
     * @param lockKey 锁的key
     */
    void unlock(String lockKey);
}
