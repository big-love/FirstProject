package com.chunfeng.common.page;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PageResult
 * @Author chunfeng
 * @Description 分页响应结果
 * @date 2026/3/18 19:34
 * @Version 1.0
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(Integer pageNum, Integer pageSize, Long total, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(total);
        result.setRecords(records);
        result.setPages((int) Math.ceil((double) total / pageSize));
        result.setHasNext(pageNum < result.getPages());
        return result;
    }

    /**
     * 空结果
     */
    public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
        return of(pageNum, pageSize, 0L, List.of());
    }
}
