package com.chunfeng.componet;

import com.chunfeng.entity.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("RedisComponent")
public class RedisComponent {
    @Autowired
    private RedisUtils redisUtils;
    
    /**
     * 检查 token 是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        String blacklistKey = Constants.REDIS_KEY_TOKEN_BLACK_LIST + token;
        return redisUtils.get(blacklistKey) != null;
    }

    /**
     * 通用的 set 方法，带过期时间
     * @param key   Redis 的键
     * @param value Redis 的值
     * @param time  过期时间（秒）
     */
    public void set(String key, Object value, long time) {
        redisUtils.setex(key, value, time);
    }

    /**
     * 设置键值对并指定过期时间（setex）
     * @param key   Redis 的键
     * @param value Redis 的值
     * @param time  过期时间（秒）
     */
    public void setex(String key, Object value, long time) {
        redisUtils.setex(key, value, time);
    }

    /**
     * 获取字符串值
     * @param redisKey Redis 的键
     * @return 字符串值
     */
    public String getString(String redisKey) {
        return redisUtils.getString(redisKey);
    }

    /**
     * 获取对象值（泛型）
     * @param key Redis 的键
     * @return 对象值
     */
    public Object get(String key) {
        return redisUtils.get(key);
    }

    /**
     * 删除键
     * @param redisKey Redis 的键
     */
    public void delete(String redisKey) {
        redisUtils.delete(redisKey);
    }

    /**
     * 删除键（del 别名）
     * @param key Redis 的键
     */
    public void del(String key) {
        redisUtils.delete(key);
    }
    
}
