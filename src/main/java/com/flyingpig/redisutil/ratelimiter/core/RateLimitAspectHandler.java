package com.flyingpig.redisutil.ratelimiter.core;


import com.flyingpig.redisutil.ratelimiter.annotation.RateLimit;
import com.flyingpig.redisutil.ratelimiter.exception.RateLimitException;
import com.flyingpig.redisutil.ratelimiter.model.Rule;
import com.flyingpig.redisutil.ratelimiter.util.RuleParser;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
public class RateLimitAspectHandler {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspectHandler.class);

    @Autowired
    private RateLimitStrategyFactory rateLimitStrategyFactory;
    @Autowired
    private RuleParser ruleProvider;


    @Around(value = "@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        Rule rule = ruleProvider.getRateLimiterRule(joinPoint, rateLimit);
        boolean allowed = rateLimitStrategyFactory.isAllowed(rule);
        if (!allowed) {
            logger.info("Trigger current limiting,key:{}", rule.getLimitKey());
            throw new RateLimitException("Too Many Requests:" + rule.getMode());
        }
        return joinPoint.proceed();
    }


}
