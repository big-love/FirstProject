package com.chunfeng.dto.request.file;

import lombok.Data;

/**
 * @ClassName RenameRequest
 * @Author chunfeng
 * @Description
 * @date 2026/3/21 11:30
 * @Version 1.0
 */
@Data
public class RenameRequest {

    /**
     * 文件 ID（如果是新建文件夹则为空）
     */
    private String fileId;

    /**
     * 父目录 ID
     */
    private String filePid;

    /**
     * 新文件名（不含后缀，如果是文件夹则包含完整名称）
     */
    private String fileName;
}