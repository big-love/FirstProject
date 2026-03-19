package com.chunfeng.dto.request.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName UploadFileRequest
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:26
 * @Version 1.0
 */
@Data
public class UploadFileRequest {

    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @NotBlank(message = "文件MD5不能为空")
    private String fileMd5;

    @NotNull(message = "分片索引不能为空")
    private Integer chunkIndex;

    @NotNull(message = "分片总数不能为空")
    private Integer chunks;

    private String fileId;

    @NotBlank(message = "父级ID不能为空")
    private String filePid;
}
