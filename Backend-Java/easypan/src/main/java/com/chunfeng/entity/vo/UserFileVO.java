package com.chunfeng.entity.vo;

/**
 * @ClassName UserFileVO
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 19:46
 * @Version 1.0
 */

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户文件视图对象
 */
@Data
public class UserFileVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 业务文件ID
     */
    private String fileId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 物理文件ID
     */
    private Long storageId;

    /**
     * 父目录ID
     */
    private String filePid;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件大小（可读格式）
     */
    private String fileSizeFormat;

    /**
     * 文件类型：0文件 1目录
     */
    private Integer folderType;

    /**
     * 文件类型描述
     */
    private String folderTypeDesc;

    /**
     * 删除标记：0删除 1回收站 2正常
     */
    private Integer delFlag;

    /**
     * 删除标记描述
     */
    private String delFlagDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;
}