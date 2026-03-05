package com.chunfeng.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName RefreshTokenResponse
 * @Author chunfeng
 * @Description  刷新 Token 响应 - 仅包含新 token
 * @date 2026/3/3 10:16
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireTime;
    private Long refreshTokenExpireTime;
}
