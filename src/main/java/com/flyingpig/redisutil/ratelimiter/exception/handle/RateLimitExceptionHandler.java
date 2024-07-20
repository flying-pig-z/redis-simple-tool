package com.flyingpig.redisutil.ratelimiter.exception.handle;;
import com.flyingpig.redisutil.ratelimiter.exception.RateLimitException;
import com.flyingpig.redisutil.ratelimiter.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//全局异常处理器
@Slf4j
@RestControllerAdvice
public class RateLimitExceptionHandler {
    /**
     * 接口访问过于频繁--2
     * @param e
     * @return
     */
    @ExceptionHandler(RateLimitException.class)
    public Result rateLimitExceptionHandler(Exception e){
        log.error("接口访问过于频繁");
        return Result.error(500,"接口访问过于频繁");
    }
}
