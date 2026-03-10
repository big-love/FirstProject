package com.chunfeng.componet;

import com.chunfeng.entity.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("RedisComponent")
public class RedisComponent {
    @Autowired
    private RedisUtils redisUtils;
    /**
     * 检查token是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        String blacklistKey = Constants.REDIS_KEY_TOKEN_BLACK_LIST + token;
        return redisUtils.get(blacklistKey) != null;
    }

    /**
     * 【新增】通用的 set 方法，带过期时间
     * * @param key   Redis的键
     * @param value Redis的值
     * @param time  过期时间（单位通常是秒，具体看你 redisUtils 的实现）
     */
    public void set(String key, Object value, long time) {
        redisUtils.setex(key, value, time);
    }

    public String getString(String redisKey) {
        return redisUtils.getString(redisKey);
    }

    public void delete(String redisKey) {
        redisUtils.delete(redisKey);
    }
}
