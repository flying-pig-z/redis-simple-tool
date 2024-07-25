package com.flyingpig.redisutil.config;

import com.flyingpig.redisutil.lock.improvedLock.ImprovedRedisLockV1;
import com.flyingpig.redisutil.lock.improvedLock.ImprovedRedisLockV2;
import com.flyingpig.redisutil.lock.improvedLock.ImprovedRedisLockV3;
import com.flyingpig.redisutil.lock.improvedLock.ImprovedRedisLockV4;
import com.flyingpig.redisutil.lock.simpleLock.SimpleRedisLockV1;
import com.flyingpig.redisutil.lock.simpleLock.SimpleRedisLockV2;
import com.flyingpig.redisutil.lock.simpleLock.SimpleRedisLockV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockAutoConfiguration {
    @Bean
    public SimpleRedisLockV1 simpleRedisLockV1(){
        return new SimpleRedisLockV1();
    }

    @Bean
    public SimpleRedisLockV2 simpleRedisLockV2(){
        return new SimpleRedisLockV2();
    }

    @Bean
    public SimpleRedisLockV3 simpleRedisLockV3(){
        return new SimpleRedisLockV3();
    }

    @Bean
    public ImprovedRedisLockV1 reentrantRedisLockV1(){
        return new ImprovedRedisLockV1();
    }

    @Bean
    public ImprovedRedisLockV2 reentrantRedisLockV2(){
        return new ImprovedRedisLockV2();
    }

    @Bean
    public ImprovedRedisLockV3 reentrantRedisLockV3(){
        return new ImprovedRedisLockV3();
    }

    @Bean
    public ImprovedRedisLockV4 reentrantRedisLockV4(){
        return new ImprovedRedisLockV4();
    }
}
