package com.chunfeng.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName UserInfoVo
 * @Author chunfeng
 * @Description
 * @date 2025/11/19 15:57
 * @Version 1.0
 */
@Data
public class UserInfoVo implements Serializable {
    private String userId;
    private String nickName;
    private String email;
    private String qqAvatar;
    /**
     * 用户创建时间
     */
    @JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy_MM_dd HH:mm:ss")
    private Date joinTime;

    /**
     * 最后一次登录时间
     */
    @JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy_MM_dd HH:mm:ss")
    private Date lastLoginTime;

    /**
     * 账号状态，0表示禁用，1表示使用
     */
    private Integer status;

    /**
     * 该账号已使用空间
     */
    private Long userSpace;

    /**
     * 该账号全部空间
     */
    private Long totalSpace;

}
