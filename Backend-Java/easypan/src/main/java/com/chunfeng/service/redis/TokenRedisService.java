package com.chunfeng.service.redis;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Token Redis 管理服务
 *
 * <p>主要功能：
 * 1. 存储和校验用户 Token
 * 2. 管理用户的 Token 集合，支持多端登录控制
 * 3. 缓存用户基础信息，减少数据库访问
 * 4. 提供 Token 失效与过期管理机制
 *
 * @author chunfeng
 * @date 2026/3/5
 * @version 1.0
 */
@Service
public class TokenRedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis Key前缀常量
    private static final String ACCESS_TOKEN_PREFIX = "auth:access:";
    private static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";
    private static final String USER_TOKENS_PREFIX = "auth:user:tokens:";
    private static final String USER_INFO_PREFIX = "auth:user:info:";
    private static final String TOKEN_BLACKLIST_PREFIX = "auth:blacklist:";

    /**
     * 保存AccessToken
     *
     * @param accessToken token字符串
     * @param userId 用户ID
     * @param expireMinutes 过期时间（分钟）
     */
    public void saveAccessToken(String accessToken, Long userId, long expireMinutes) {
        String key = ACCESS_TOKEN_PREFIX + accessToken;
        stringRedisTemplate.opsForValue().set(key, userId.toString(), expireMinutes, TimeUnit.MINUTES);

        // 同时将token添加到用户的token集合中
        addTokenToUserSet(userId, accessToken, expireMinutes);
    }

    /**
     * 保存RefreshToken
     *
     * @param refreshToken token字符串
     * @param userId 用户ID
     * @param expireDays 过期时间（天）
     */
    public void saveRefreshToken(String refreshToken, Long userId, long expireDays) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        stringRedisTemplate.opsForValue().set(key, userId.toString(), expireDays, TimeUnit.DAYS);
    }

    /**
     * 验证AccessToken并获取userId
     *
     * @param accessToken token字符串
     * @return userId，如果token无效返回null
     */
    public Long validateAccessToken(String accessToken) {
        // 先检查黑名单
        if (isTokenInBlacklist(accessToken)) {
            return null;
        }

        String key = ACCESS_TOKEN_PREFIX + accessToken;
        String userId = stringRedisTemplate.opsForValue().get(key);
        return userId != null ? Long.parseLong(userId) : null;
    }

    /**
     * 验证RefreshToken并获取userId
     *
     * @param refreshToken token字符串
     * @return userId，如果token无效返回null
     */
    public Long validateRefreshToken(String refreshToken) {
        if (isTokenInBlacklist(refreshToken)) {
            return null;
        }

        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        String userId = stringRedisTemplate.opsForValue().get(key);
        return userId != null ? Long.parseLong(userId) : null;
    }

    /**
     * 将token添加到用户的token集合
     * 用于管理用户的所有有效token
     */
    private void addTokenToUserSet(Long userId, String token, long expireMinutes) {
        String key = USER_TOKENS_PREFIX + userId;
        stringRedisTemplate.opsForSet().add(key, token);
        // 设置过期时间为最长的token过期时间
        stringRedisTemplate.expire(key, expireMinutes, TimeUnit.MINUTES);
    }

    /**
     * 删除单个AccessToken
     * 用于用户登出
     */
    public void deleteAccessToken(String accessToken) {
        String key = ACCESS_TOKEN_PREFIX + accessToken;
        stringRedisTemplate.delete(key);
    }

    /**
     * 删除RefreshToken
     */
    public void deleteRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        stringRedisTemplate.delete(key);
    }

    /**
     * 删除用户的所有token
     * 用于修改密码后强制登出所有设备
     *
     * @param userId 用户ID
     */
    public void deleteAllUserTokens(Long userId) {
        String userTokensKey = USER_TOKENS_PREFIX + userId;
        Set<String> tokens = stringRedisTemplate.opsForSet().members(userTokensKey);

        if (tokens != null && !tokens.isEmpty()) {
            for (String token : tokens) {
                deleteAccessToken(token);
            }
        }

        // 删除用户token集合
        stringRedisTemplate.delete(userTokensKey);

        // 删除用户信息缓存
        deleteUserInfoCache(userId);
    }

    /**
     * 将token加入黑名单
     * 用于提前失效某个token（比如检测到异常使用）
     *
     * @param token token字符串
     * @param remainingSeconds token剩余有效时间（秒）
     */
    public void addToBlacklist(String token, long remainingSeconds) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        stringRedisTemplate.opsForValue().set(key, "1", remainingSeconds, TimeUnit.SECONDS);
    }

    /**
     * 检查token是否在黑名单中
     */
    public boolean isTokenInBlacklist(String token) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 缓存用户信息
     * 减少数据库查询
     *
     * @param userId 用户ID
     * @param userInfo 用户信息对象
     * @param expireMinutes 过期时间（分钟）
     */
    public void cacheUserInfo(Long userId, Object userInfo, long expireMinutes) {
        String key = USER_INFO_PREFIX + userId;
        redisTemplate.opsForValue().set(key, userInfo, expireMinutes, TimeUnit.MINUTES);
    }

    /**
     * 获取缓存的用户信息
     *
     * @param userId 用户ID
     * @return 用户信息对象，如果不存在返回null
     */
    public Object getUserInfoCache(Long userId) {
        String key = USER_INFO_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除用户信息缓存
     * 用于用户信息更新后
     */
    public void deleteUserInfoCache(Long userId) {
        String key = USER_INFO_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * 获取用户的所有有效token数量
     * 可用于限制同时登录设备数
     */
    public Long getUserTokenCount(Long userId) {
        String key = USER_TOKENS_PREFIX + userId;
        return stringRedisTemplate.opsForSet().size(key);
    }

    /**
     * 刷新token的过期时间
     * 用于"记住我"功能或活跃用户自动续期
     */
    public void refreshTokenExpire(String accessToken, long expireMinutes) {
        String key = ACCESS_TOKEN_PREFIX + accessToken;
        stringRedisTemplate.expire(key, expireMinutes, TimeUnit.MINUTES);
    }
}
