package com.chunfeng.mapper;

import com.chunfeng.entity.po.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @ClassName UserMapper
 * @Author chunfeng
 * @Description
 * @date 2026/3/3 09:37
 * @Version 1.0
 */
@Mapper
public interface UserMapper {

    /*
    * @description: 查找用户信息通过邮箱
    * @param email
    * @return UserInfo
    *
    * */
    @Select("select * from user_info where email = #{email}")
    UserInfo selectByEmail(@Param("email") String email);

    /*
    * @description: 获取用户信息通过用户ID
    * @param userId
    * @return UserInfo
    *
    * */
    @Select("select * from user_info where user_id = #{userId}")
    UserInfo selectById(Long userId);

    /*
    * @description: 修改用户信息通过用户ID
    * @param userInfo
    * @return void
    *
    * */
    @Select("update user set nick_name = #{nickName}, qq_avatar = #{qqAvatar}, status = #{status} where user_id = #{userId}")
    void updateById(UserInfo userInfo);
}
