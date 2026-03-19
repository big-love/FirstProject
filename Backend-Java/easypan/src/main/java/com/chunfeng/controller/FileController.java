package com.chunfeng.controller;

import com.chunfeng.componet.RedisComponent;
import com.chunfeng.componet.RedisUtils;
import com.chunfeng.config.AppConfig;
import com.chunfeng.dto.request.file.UploadFileRequest;
import com.chunfeng.dto.response.file.UploadResponse;
import com.chunfeng.security.JwtUtil;
import com.chunfeng.service.file.FileService;
import com.chunfeng.utils.CommonUtils;
import com.chunfeng.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName FileController
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:11
 * @Version 1.0
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CommonUtils commonUtils;

    // 文件上传接口
    @PostMapping("/uploadFile")
    public Result<UploadResponse> uploadFile(@Valid UploadFileRequest request, HttpServletRequest httpServletRequest) {
        Long userId = jwtUtil.getUserIdFromAccessToken(commonUtils.extractToken(httpServletRequest));
        UploadResponse response = fileService.uploadFile(request, userId);
        return Result.success(response);
    }
}
