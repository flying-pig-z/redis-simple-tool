package com.flyingpig.redisutil.ratelimiter.core;

import com.flyingpig.redisutil.ratelimiter.core.strategy.FixedWindowLimitStrategy;
import com.flyingpig.redisutil.ratelimiter.core.strategy.RateLimitStrategy;
import com.flyingpig.redisutil.ratelimiter.core.strategy.SlideWindowLimitStrategy;
import com.flyingpig.redisutil.ratelimiter.core.strategy.TokenBucketLimitStrategy;
import com.flyingpig.redisutil.ratelimiter.model.Mode;
import com.flyingpig.redisutil.ratelimiter.model.Rule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
@Component
public class RateLimitStrategyFactory {
    private static final Map<Mode, RateLimitStrategy> rateLimitStrategies = new HashMap();// 策略存储容器

    @Autowired
    private SlideWindowLimitStrategy slideWindowLimitStrategy;
    @Autowired
    private FixedWindowLimitStrategy fixedWindowLimitStrategy;
    @Autowired
    private TokenBucketLimitStrategy tokenBucketLimitStrategy;


    @PostConstruct
    public void init() {
        rateLimitStrategies.put(Mode.SLIDE_WINDOW, slideWindowLimitStrategy);
        rateLimitStrategies.put(Mode.FIXED_WINDOW, fixedWindowLimitStrategy);
        rateLimitStrategies.put(Mode.TOKEN_BUCKET, tokenBucketLimitStrategy);
    }

    public boolean isAllowed(Rule rule) {
        return rateLimitStrategies.get(rule.getMode()).isAllowed(rule);
    }

}
