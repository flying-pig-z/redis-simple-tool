-- resources/tryLock.lua
-- 尝试获取分布式锁的 Lua 脚本
-- KEYS[1]: 锁名称
-- ARGV[1]: 锁失效时间（毫秒）
-- ARGV[2]: 线程标识（格式为 id + ":" + threadId）

if (redis.call('exists', KEYS[1]) == 0) then
    -- 锁不存在：创建锁
    redis.call('hset', KEYS[1], ARGV[2], 1);  -- 将线程标识作为字段，设置初始值为 1
    redis.call('pexpire', KEYS[1], ARGV[1]);  -- 设置锁的过期时间（毫秒）
    return 1;  -- 返回 1 表示成功获取锁
elseif (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then
    -- 锁已存在，并且当前线程已经持有该锁
    redis.call('hincrby', KEYS[1], ARGV[2], 1);  -- 增加锁计数
    redis.call('pexpire', KEYS[1], ARGV[1]);  -- 更新锁的过期时间
    return 1;  -- 返回 1 表示成功
else
    -- 锁已存在，但当前线程未持有该锁
    return 0;  -- 返回 0 表示获取锁失败
end
