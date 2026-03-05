package com.chunfeng.entity.dto;

import lombok.Data;

/**
 * @ClassName SessionWebUserDto
 * @Author chunfeng
 * @Description
 * @date 2025/10/22 15:30
 * @Version 1.0
 */
@Data
public class SessionWebUserDto {
    private String userId;
    private String nickName;
    private boolean admin;
    private String avatar;
}
