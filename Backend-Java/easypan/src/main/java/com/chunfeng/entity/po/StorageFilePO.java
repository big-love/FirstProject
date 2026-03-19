package com.chunfeng.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName StorageFilePO
 * @Author chunfeng
 * @Description 物理文件存储表
 * @date 2026/3/18 19:35
 * @Version 1.0
 */
@Data
public class StorageFilePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物理文件ID
     */
    private Long id;

    /**
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 物理路径
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
}