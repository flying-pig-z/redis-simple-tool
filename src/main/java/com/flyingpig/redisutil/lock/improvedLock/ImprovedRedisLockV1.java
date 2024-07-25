package com.flyingpig.redisutil.lock.improvedLock;

import com.flyingpig.redisutil.lock.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
    增加可重入功能
 */
@Component
public class ImprovedRedisLockV1 implements Lock {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 使用ThreadLocal来存储每个线程的锁计数
    private ThreadLocal<Integer> lockCount = ThreadLocal.withInitial(() -> 0);

    // 线程标识
    private static final String ID_PREFIX = UUID.randomUUID() + "-";


    @Override
    public boolean tryLock(String lockKey, long timeoutSec) {
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        // 当前线程已经持有锁，则增加计数并返回true
        if (lockCount.get() > 0) {
            lockCount.set(lockCount.get() + 1);
            return true;
        }
        // 尝试获取锁
        boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, threadId, timeoutSec, TimeUnit.SECONDS);
        if (locked) {
            // 获取成功，计数器加1
            lockCount.set(1);
        }
        return locked;
    }

    @Override
    public void unlock(String lockKey) {
        Integer currentCount = lockCount.get();

        if (currentCount > 0) {
            // 减少计数器
            currentCount--;

            if (currentCount == 0) {
                // 如果计数器为0，释放锁
                stringRedisTemplate.delete(lockKey);
            } else {
                // 更新计数器
                lockCount.set(currentCount);
            }
        }
    }
}


