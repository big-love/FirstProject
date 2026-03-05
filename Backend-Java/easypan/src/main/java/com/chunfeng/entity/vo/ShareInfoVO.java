package com.chunfeng.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName ShareInfoVo
 * @Author chunfeng
 * @Description
 * @date 2025/11/20 10:32
 * @Version 1.0
 */
@Data
public class ShareInfoVO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date shareTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;
    private String nickName;
    private String fileName;
    private Boolean currentUser;
    private String fileId;
    private String avatar;
    private String userId;
}
