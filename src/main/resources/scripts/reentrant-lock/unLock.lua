-- unlock.lua
-- KEYS[1]: 锁名称，即 Redis 中的哈希表的键名
-- ARGV[1]: 线程标识，格式为 id:threadId，用于识别当前释放锁的线程

-- 检查当前线程是否持有指定的锁
if (redis.call('hexists', KEYS[1], ARGV[1]) == 0) then
    return nil;  -- 如果当前线程未持有锁，则直接返回 nil
end

-- 当前线程持有锁，减少该锁的计数
local count = redis.call('hincrby', KEYS[1], ARGV[1], -1);

-- 如果计数减至 0，则表示当前线程不再持有该锁
if (count == 0) then
    redis.call('hdel', KEYS[1], ARGV[1]);  -- 从哈希表中删除当前线程的锁记录
    -- 检查哈希表是否为空，如果为空，则删除整个锁
    if (redis.call('hlen', KEYS[1]) == 0) then
        redis.call('del', KEYS[1]);  -- 删除整个哈希表，即删除锁
    end
end

return count;  -- 返回当前锁的计数，如果为 0，则说明成功释放了锁
