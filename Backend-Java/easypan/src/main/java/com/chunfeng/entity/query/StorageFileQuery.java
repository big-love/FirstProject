package com.chunfeng.entity.query;

import com.chunfeng.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName StorageFileQuery
 * @Author chunfeng
 * @Description 存储文件查询对象
 * @date 2026/3/18 19:37
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StorageFileQuery extends PageQuery {

    private static final long serialVersionUID = 1L;

    /**
     * 文件MD5（精确匹配）
     */
    private String fileMd5;

    /**
     * 文件大小最小值
     */
    private Long fileSizeMin;

    /**
     * 文件大小最大值
     */
    private Long fileSizeMax;

    /**
     * 文件路径（模糊匹配）
     */
    private String filePathLike;

    /**
     * 引用次数最小值
     */
    private Integer refCountMin;

    /**
     * 创建时间开始
     */
    private String createTimeStart;

    /**
     * 创建时间结束
     */
    private String createTimeEnd;

    private Integer fileCategory;

    private Integer fileType;
}
