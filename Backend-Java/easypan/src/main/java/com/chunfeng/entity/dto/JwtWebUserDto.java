package com.chunfeng.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName JwtWebUserDto
 * @Author chunfeng
 * @Description
 * @date 2025/12/15 20:19
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class JwtWebUserDto {
    private String userId;
    private String nickName;
    private boolean admin;
    private String avatar;

    private String accessToken;

    private String refreshToken;
}
