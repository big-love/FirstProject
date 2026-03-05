package com.chunfeng.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @ClassName RefreshTokenRequest
 * @Author chunfeng
 * @Description 刷新 Token 请求 - 用于获取新的访问令牌
 * @date 2026/3/3 10:16
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class RefreshTokenRequest {

    /**
     * 刷新令牌
     * 用于验证用户身份并获取新的 access token
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}
