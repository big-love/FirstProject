package com.chunfeng.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName JwtProperties
 * @Author chunfeng
 * @Description jwt配置文件
 * @date 2025/12/2 20:33
 * @Version 1.0
 */

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String accessSecret;
    private String refreshSecret;
    private long accessExpiration;
    private long refreshExpiration;
    private long maxExpiration;
}

