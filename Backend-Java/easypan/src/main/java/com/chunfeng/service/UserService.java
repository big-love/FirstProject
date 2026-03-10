package com.chunfeng.service;


/**
 * 用户服务接口
 */
public interface UserService {


    /**
     * 修改密码并登出所有设备
     */
    void changePasswordAndLogoutAll(Long userId, String oldPassword, String newPassword);
}