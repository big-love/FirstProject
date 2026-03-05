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

}
