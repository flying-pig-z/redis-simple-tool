package com.flyingpig.redisutil.ratelimiter.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;


@Component
public final class LuaScriptSelector {

    private static final DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();;


    public static DefaultRedisScript<Boolean> getFixedWindowRateLimiterScript() {
        redisScript.setResultType(Boolean.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/limit/fix_window_limit.lua")));
        return redisScript;
    }

    public static DefaultRedisScript<Boolean> getSlideWindowRateLimiterScript() {
        redisScript.setResultType(Boolean.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/limit/slide_window_limit.lua")));
        return redisScript;
    }

    public static DefaultRedisScript<Boolean> getTokenBucketRateLimiterScript() {
        redisScript.setResultType(Boolean.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/limit/token_bucket_limit.lua")));
        return redisScript;
    }
}
