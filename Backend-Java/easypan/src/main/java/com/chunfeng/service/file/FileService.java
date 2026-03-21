package com.chunfeng.service.file;

import com.chunfeng.common.page.PageResult;
import com.chunfeng.dto.request.file.UploadFileRequest;
import com.chunfeng.dto.response.file.FilePreviewResponse;
import com.chunfeng.dto.response.file.UploadResponse;
import com.chunfeng.entity.po.UserFilePO;
import com.chunfeng.entity.query.UserFileQuery;
import com.chunfeng.entity.vo.UserFileVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;


/**
 * @ClassName FileService
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:28
 * @Version 1.0
 */
public interface FileService {

    /**
     * 加载文件列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<UserFileVO> loadFileList(UserFileQuery query);

    /**
     * 文件上传
     *
     * @param request 文件上传请求
     * @param userId  用户 ID
     * @return 文件上传响应
     */
    UploadResponse uploadFile(UploadFileRequest request, Long userId);

    /**
     * 获取文件预览信息
     *
     * @param fileId 文件 ID
     * @param userId 用户 ID
     * @return 文件预览响应
     */
    FilePreviewResponse getFilePreview(String fileId, Long userId);

    /**
     * 获取文件内容（预览/下载用）
     *
     * @param
     * @param userId 用户 ID
     * @param response HTTP 响应对象
     */
    void getFile(Long id, Long userId, HttpServletResponse response);

    /**
     * 获取视频分片信息（HLS 流媒体）
     *
     * @param
     * @param userId 用户 ID
     * @param response HTTP 响应对象
     */
    void getVideoInfo(Long id, Long userId, HttpServletResponse response);

    /**
     * 创建下载链接
     *
     * @param
     * @param userId 用户 ID
     * @return 下载码
     */
    String createDownloadUrl(Long id, Long userId);

    /**
     * 执行文件下载
     *
     * @param downloadCode 下载码
     * @param response HTTP 响应对象
     */
    void download(String downloadCode, HttpServletResponse response);

    
    UserFilePO getFileInfo(String fileId, Long userId);
    
    @Data
    class FileInfo {
        private String fileName;
        private Integer fileType;
    }
}
