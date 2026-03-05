package com.chunfeng.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @ClassName JwtUtil
 * @Author chunfeng
 * @Description Jwt工具类
 * @date 2025/12/2 20:33
 * @Version 1.0
 */
@Component
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.accessKey = Keys.hmacShaKeyFor(jwtProperties.getAccessSecret().getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(jwtProperties.getRefreshSecret().getBytes(StandardCharsets.UTF_8));
    }

    public long getAccessTokenExpireDuration() {
        return jwtProperties.getAccessExpiration() * 1000;
    }

    public long getRefreshTokenExpireDuration() {
        return jwtProperties.getRefreshExpiration() * 1000;
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId) {
        return buildToken(userId, jwtProperties.getAccessExpiration(), accessKey, "access");
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId) {
        return buildToken(userId, jwtProperties.getRefreshExpiration(), refreshKey, "refresh");
    }

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param expireSeconds 过期时间（秒）
     * @param key 签名密钥
     */
    private String buildToken(Long userId, long expireSeconds, SecretKey key, String tokenType) {
        long expireMs = expireSeconds * 1000;
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expireMs))
            .claim("type", tokenType)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * 从访问令牌中获取用户ID
     */
    public Long getUserIdFromAccessToken(String token) {
        return Long.valueOf(parseClaims(token, accessKey).getSubject());
    }

    /**
     * 从刷新令牌中获取用户ID
     */
    public Long getUserIdFromRefreshToken(String token) {
        return Long.valueOf(parseClaims(token, refreshKey).getSubject());
    }

    /**
     * 验证访问令牌
     */
    public boolean validateAccessToken(String token) {
        return validate(token, accessKey);
    }

    /**
     * 验证刷新令牌
     */
    public boolean validateRefreshToken(String token) {
        return validate(token, refreshKey);
    }

    /**
     * 验证令牌有效性
     */
    private boolean validate(String token, SecretKey key) {
        try {
            parseClaims(token, key);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取访问令牌剩余有效时间
     */
    public long getAccessTokenRemainingSeconds(String token) {
        return getTokenRemainingSeconds(token, accessKey);
    }

    /**
     * 获取令牌剩余有效时间
     */
    private long getTokenRemainingSeconds(String token, SecretKey key) {
        try {
            Claims claims = parseClaims(token, key);
            Date expiration = claims.getExpiration();
            long remainingMs = expiration.getTime() - System.currentTimeMillis();
            return remainingMs > 0 ? remainingMs / 1000 : 0;
        } catch (JwtException e) {
            return 0;
        }
    }

    /**
     * 解析JWT令牌
     */
    private Claims parseClaims(String token, SecretKey key) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
