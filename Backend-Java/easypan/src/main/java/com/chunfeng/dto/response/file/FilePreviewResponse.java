package com.chunfeng.dto.response.file;

import lombok.Data;

/**
 * @ClassName FilePreviewResponse
 * @Author chunfeng
 * @Description
 * @date 2026/3/20 16:17
 * @Version 1.0
 */
@Data
public class FilePreviewResponse {

    /**
     * 文件 ID
     */
    private String fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型 (1:视频，2:音频，3:图片，4:PDF, 5:Word, 6:Excel, 7:文本，8:其他)
     */
    private Integer fileType;

    /**
     * 文件分类 (1:视频，2:音频，3:图片，4:文档，5:其他)
     */
    private Integer fileCategory;

    /**
     * 预览类型 (url: 链接预览，content: 内容预览)
     */
    private String previewType;

    /**
     * 预览 URL（用于图片、视频、音频、PDF 等）
     */
    private String previewUrl;

    /**
     * 文件内容（用于文本文件直接预览）
     */
    private String content;

    /**
     * 文件大小
     */
    private Long fileSize;
}
