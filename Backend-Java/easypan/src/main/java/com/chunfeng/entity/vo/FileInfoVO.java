package com.chunfeng.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @ClassName FileInfoVO
 * @Author chunfeng
 * @Description
 * @date 2025/10/27 15:23
 * @Version 1.0
 */
@Data
public class FileInfoVO {
    /**
     * 文件ID
     */
    private String fileId;


    /**
     * 父级id
     */
    private String filePid;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件封面
     */
    private String fileCover;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy_MM_dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy_MM_dd HH:mm:ss")
    private Date lastUpdateTime;

    /**
     * 0:文件 1：目录
     */
    private Integer folderType;

    /**
     * 文件分类 1:视频 2:音频 3:图片 4:文档 5:其他
     */
    private Integer fileCategory;

    /**
     * 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他
     */
    private Integer fileType;

    /**
     * 0:转码中 1:转码失败 2:转码成功
     */
    private Integer status;

    /**
     * 进入回收站时间
     */
    @JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy_MM_dd HH:mm:ss")
    private Date recoveryTime;

    /**
     * 标记删除 0:删除 1:回收站 2:正常
     */
    private Integer delFlag;


}