package com.chunfeng.dto.response.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName UploadResponse
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:30
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    private String fileId;
    private String status; // "upload_seconds" | "upload_finish" | "uploading"
}
