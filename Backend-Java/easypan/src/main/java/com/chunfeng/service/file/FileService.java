package com.chunfeng.service.file;

import com.chunfeng.common.page.PageResult;
import com.chunfeng.dto.request.file.UploadFileRequest;
import com.chunfeng.dto.response.file.UploadResponse;
import com.chunfeng.entity.query.UserFileQuery;
import com.chunfeng.entity.vo.UserFileVO;

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
}
