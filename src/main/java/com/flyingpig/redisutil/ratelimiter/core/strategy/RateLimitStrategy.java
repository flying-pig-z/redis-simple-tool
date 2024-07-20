package com.flyingpig.redisutil.ratelimiter.core.strategy;


import com.flyingpig.redisutil.ratelimiter.model.Rule;

/*
    限流策略
 */
public interface RateLimitStrategy {
    Boolean isAllowed(Rule rule);
}
