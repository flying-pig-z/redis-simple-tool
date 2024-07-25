package com.flyingpig.redisutil.config;

import com.flyingpig.redisutil.cache.core.CacheUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheUtilAutoConfiguration {
    // 注入缓存工具类
    // 通过@Bean注解将CacheUtil注入到Spring容器中
    @Bean
    public CacheUtil cacheUtil() {
        return new CacheUtil();
    }
}
