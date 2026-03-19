package com.chunfeng.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName UserFilePO
 * @Author chunfeng
 * @Description 用户文件表
 * @date 2026/3/18 19:45
 * @Version 1.0
 */
@Data
public class UserFilePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 业务文件ID
     */
    private String fileId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 物理文件ID，目录为空
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
     * 用户视角文件大小，目录为0
     */
    private Long fileSize;

    /**
     * 0文件 1目录
     */
    private Integer folderType;

    /**
     * 0删除 1回收站 2正常
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;
}
