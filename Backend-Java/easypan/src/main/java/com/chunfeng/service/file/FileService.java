package com.chunfeng.service.file;

import com.chunfeng.dto.request.file.UploadFileRequest;
import com.chunfeng.dto.response.file.UploadResponse;

/**
 * @ClassName FileService
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:28
 * @Version 1.0
 */
public interface FileService {
    /**
     * 文件上传
     *
     * @param request 文件上传请求
     * @param userId  用户ID
     * @return 文件上传响应
     */
    UploadResponse uploadFile(UploadFileRequest request, Long userId);
}
