package com.chunfeng.mapper;

import com.chunfeng.entity.po.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @ClassName FileMapper
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 19:06
 * @Version 1.0
 */
@Mapper
public interface FileMapper {

    /**
     * 根据md5查询文件信息
     *
     * @param md5 文件md5
     * @return 文件信息
     */
    @Select("select * from file_info where md5 = #{md5}")
    FileInfo findByMd5(String md5);
}
