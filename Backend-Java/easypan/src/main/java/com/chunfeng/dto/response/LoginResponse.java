package com.chunfeng.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName LoginResponse
 * @Author chunfeng
 * @Description 登录响应 - 包含完整用户信息
 * @date 2026/3/3 10:15
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class LoginResponse {
    private Long userId;
    private String nickName;
    private Boolean admin;
    private String avatar;
    private String email;

    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireTime;
    private Long refreshTokenExpireTime;
}
