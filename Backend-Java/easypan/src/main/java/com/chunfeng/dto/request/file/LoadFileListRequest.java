package com.chunfeng.dto.request.file;

import lombok.Data;

/**
 * @ClassName LoadFileListRequest
 * @Author chunfeng
 * @Description
 * @date 2026/3/19 09:41
 * @Version 1.0
 */
@Data
public class LoadFileListRequest {

    /**
     * 页码，默认第 1 页
     */
    private Integer pageNo = 1;

    /**
     * 每页数量，默认 20 条
     */
    private Integer pageSize = 20;

    /**
     * 文件名模糊搜索（可选）
     */
    private String fileNameFuzzy;

    /**
     * 文件分类：all/video/music/image/doc/others
     */
    private String category = "all";

    /**
     * 父目录 ID，默认 0（根目录）
     */
    private String filePid = "0";
}