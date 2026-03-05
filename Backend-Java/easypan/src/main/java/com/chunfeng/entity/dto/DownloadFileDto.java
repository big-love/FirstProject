package com.chunfeng.entity.dto;

import lombok.Data;

/**
 * @ClassName DownloadFileDto
 * @Author chunfeng
 * @Description
 * @date 2025/11/5 15:04
 * @Version 1.0
 */
@Data
public class DownloadFileDto {
    private String downloadCode;
    private String fileName;
    private String filePath;
}
