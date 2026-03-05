package com.chunfeng.entity.dto;

import lombok.Data;

/**
 * @ClassName QQInfoDto
 * @Author chunfeng
 * @Description
 * @date 2025/10/27 10:08
 * @Version 1.0
 */
@Data
public class QQInfoDto {
    private Integer ret;
    private String msg;
    private String nickname;
    private String figureurl_qq_1;
    private String figureurl_qq_2;
    private String gender;
}
