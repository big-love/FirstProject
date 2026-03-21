package com.chunfeng.mapper;

import com.chunfeng.entity.po.UserFilePO;
import com.chunfeng.entity.query.UserFileQuery;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @ClassName UserFileMapper
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 19:48
 * @Version 1.0
 */
@Mapper
public interface UserFileMapper {

    /**
     * 分页查询总数
     */
    Long countByQuery(@Param("query") UserFileQuery query);

    /**
     * 分页查询列表
     */
    List<UserFilePO> selectByQuery(@Param("query") UserFileQuery query);

    /**
     * 根据ID查询
     */
    UserFilePO selectById(@Param("id") Long id);

    /**
     * 根据业务文件ID查询
     */
    UserFilePO selectByFileId(@Param("fileId") String fileId);

    /**
     * 查询用户指定目录下的文件列表
     */
    List<UserFilePO> selectByUserAndPid(@Param("userId") String userId,
                                        @Param("filePid") String filePid,
                                        @Param("delFlag") Integer delFlag);

    /**
     * 插入
     */
    int insert(UserFilePO record);

    /**
     * 更新
     */
    int updateById(UserFilePO record);


    @Select("select * from user_file where user_id = #{userId} and file_pid = #{filePid} and file_name = #{fileName}")
    UserFilePO selectByUserIdAndPidAndName(Long userId, @NotBlank(message = "父级ID不能为空") String filePid, @NotBlank(message = "文件名不能为空") String fileName);

    /**
     * 根据文件 ID 和用户 ID 查询文件
     */
    UserFilePO selectByFileIdAndUserId(String fileId, Long userId);
}
