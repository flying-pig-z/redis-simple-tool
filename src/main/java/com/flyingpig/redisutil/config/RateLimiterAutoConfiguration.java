package com.flyingpig.redisutil.config;

import com.flyingpig.redisutil.ratelimiter.core.RateLimitAspectHandler;
import com.flyingpig.redisutil.ratelimiter.core.RateLimitStrategyFactory;
import com.flyingpig.redisutil.ratelimiter.core.strategy.FixedWindowLimitStrategy;
import com.flyingpig.redisutil.ratelimiter.core.strategy.SlideWindowLimitStrategy;
import com.flyingpig.redisutil.ratelimiter.core.strategy.TokenBucketLimitStrategy;
import com.flyingpig.redisutil.ratelimiter.exception.handle.RateLimitExceptionHandler;
import com.flyingpig.redisutil.ratelimiter.util.RuleParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterAutoConfiguration {
    @Bean
    public RateLimitAspectHandler rateLimitAspectHandler() {
        return new RateLimitAspectHandler();
    }

    @Bean
    public RateLimitStrategyFactory rateLimitStrategyFactory() {
        return new RateLimitStrategyFactory();
    }

    @Bean
    public SlideWindowLimitStrategy slideWindowLimitStrategy() {
        return new SlideWindowLimitStrategy();
    }

    @Bean
    public FixedWindowLimitStrategy fixedWindowLimitStrategy() {
        return new FixedWindowLimitStrategy();
    }

    @Bean
    public TokenBucketLimitStrategy tokenBucketLimitStrategy() {
        return new TokenBucketLimitStrategy();
    }

    @Bean
    public RuleParser ruleParser() {
        return new RuleParser();
    }

    @Bean
    public RateLimitExceptionHandler rateLimitExceptionHandler() {
        return new RateLimitExceptionHandler();
    }
}
