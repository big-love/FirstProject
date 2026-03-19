package com.chunfeng.entity.query;

import com.chunfeng.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName UserFileQuery
 * @Author chunfeng
 * @Description 用户文件查询对象
 * @date 2026/3/18 19:47
 * @Version 1.0
 */


@Data
@EqualsAndHashCode(callSuper = true)
public class UserFileQuery extends PageQuery {

    private static final long serialVersionUID = 1L;

    /**
     * 业务文件ID
     */
    private String fileId;

    /**
     * 用户ID（必填）
     */
    private String userId;

    /**
     * 父目录ID
     */
    private String filePid;

    /**
     * 文件名（模糊匹配）
     */
    private String fileNameLike;

    /**
     * 文件类型：0文件 1目录
     */
    private Integer folderType;

    /**
     * 删除标记：0删除 1回收站 2正常
     */
    private Integer delFlag;

    /**
     * 物理文件ID
     */
    private Long storageId;

    /**
     * 文件大小最小值
     */
    private Long fileSizeMin;

    /**
     * 文件大小最大值
     */
    private Long fileSizeMax;

    /**
     * 创建时间开始
     */
    private String createTimeStart;

    /**
     * 创建时间结束
     */
    private String createTimeEnd;

    /**
     * 更新时间开始
     */
    private String updateTimeStart;

    /**
     * 更新时间结束
     */
    private String updateTimeEnd;
}