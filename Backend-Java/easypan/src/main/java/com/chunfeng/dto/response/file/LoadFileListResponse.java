package com.chunfeng.dto.response.file;

import lombok.Data;

import java.util.List;

/**
 * @ClassName LoadFileListResponse
 * @Author chunfeng
 * @Description
 * @date 2026/3/19 10:26
 * @Version 1.0
 */
@Data
public class LoadFileListResponse {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页
     */
    private Integer pageNo;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 文件列表
     */
    private List<FileItemVO> list;

    /**
     * 文件项视图对象
     */
    @Data
    public static class FileItemVO {

        /**
         * 文件 ID
         */
        private Long id;

        /**
         * 文件 ID
         */
        private Long fileId;

        /**
         * 文件名（含扩展名）
         */
        private String fileName;

        /**
         * 文件大小（字节）
         */
        private Long fileSize;

        /**
         * 文件类型：1-视频，2-音频，3-图片，4-文档，5-其他
         */
        private Integer fileType;

        private Integer fileCategory;

        /**
         * 0-文件，1-文件夹
         */
        private Integer folderType;

        /**
         * 封面图 URL（视频/图片有）
         */
        private String fileCover;

        /**
         * 文件后缀
         */
        private String fileSuffix;

        /**
         * 状态：0-转码中，1-转码失败，2-正常
         */
        private Integer status;

        /**
         * 最后修改时间
         */
        private String lastUpdateTime;

        /**
         * 是否显示操作按钮（前端控制，默认 false）
         */
        private Boolean showOp = false;

        /**
         * 是否显示编辑框（前端控制，默认 false）
         */
        private Boolean showEdit = false;
    }
}
