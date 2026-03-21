package com.chunfeng.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName StorageFileVO
 * @Author chunfeng
 * @Description 存储文件视图对象
 * @date 2026/3/18 19:36
 * @Version 1.0
 */
@Data
public class StorageFileVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件大小（可读格式，如：1.5MB）
     */
    private String fileSizeFormat;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 引用次数
     */
    private Integer refCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private Integer fileCategory;

    private Integer fileType;
}