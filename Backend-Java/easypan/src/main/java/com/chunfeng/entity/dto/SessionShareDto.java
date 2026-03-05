package com.chunfeng.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName SessionShareDto
 * @Author chunfeng
 * @Description
 * @date 2025/11/20 14:47
 * @Version 1.0
 */
@Data
public class SessionShareDto {
    private String shareId;
    private String shareUserId;
    private Date expireTime;
    private String fileId;
}
