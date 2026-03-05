package com.chunfeng.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UploadResultDto
 * @Author chunfeng
 * @Description 上传过程中与前端交互的类
 * @date 2025/10/27 16:23
 * @Version 1.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResultDto implements Serializable {
    private String fileId;
    private String status;
}
