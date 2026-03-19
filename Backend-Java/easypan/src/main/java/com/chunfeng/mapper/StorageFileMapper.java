package com.chunfeng.mapper;

import com.chunfeng.entity.po.StorageFilePO;
import com.chunfeng.entity.query.StorageFileQuery;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @ClassName StorageFileMapper
 * @Author chunfeng
 * @Description 存储文件Mapper
 * @date 2026/3/18 19:38
 * @Version 1.0
 */
@Mapper
public interface StorageFileMapper {

    /**
     * 分页查询总数
     */
    Long countByQuery(@Param("query") StorageFileQuery query);

    /**
     * 分页查询列表
     */
    List<StorageFilePO> selectByQuery(@Param("query") StorageFileQuery query);

    /**
     * 根据ID查询
     */
    StorageFilePO selectById(@Param("id") Long id);

    /**
     * 根据MD5查询
     */
    StorageFilePO selectByMd5(@Param("fileMd5") String fileMd5);

    /**
     * 插入
     */
    int insert(StorageFilePO record);

    /**
     * 更新引用次数
     */
    int updateRefCount(@Param("id") Long id, @Param("increment") Integer increment);
}