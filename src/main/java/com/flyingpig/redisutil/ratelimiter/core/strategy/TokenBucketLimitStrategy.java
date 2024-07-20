package com.flyingpig.redisutil.ratelimiter.core.strategy;

import com.flyingpig.redisutil.ratelimiter.model.Rule;
import com.flyingpig.redisutil.ratelimiter.util.LuaScriptSelector;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
   令牌桶算法
 */
@Component
public class TokenBucketLimitStrategy implements RateLimitStrategy {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Boolean isAllowed(Rule rule) {
        String key = "rate_limiter:token_bucket:" + rule.getLimitKey();
        RedisScript<Boolean> redisScript = LuaScriptSelector.getSlideWindowRateLimiterScript();

        // 构造参数列表，确保参数类型正确
        List<String> keys = Collections.singletonList(key);
        List<Object> args = Arrays.asList(rule.getBucketCapacity() + "", rule.getGenerateTokenRate() + "", rule.getConsumeTokenPerReq() + "");

        // 执行Lua脚本
        return redisTemplate.execute(redisScript, keys, args.toArray());
    }
}
