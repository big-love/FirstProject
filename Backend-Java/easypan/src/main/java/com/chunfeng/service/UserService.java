package com.chunfeng.service;


import com.chunfeng.dto.response.LoginResponse;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    LoginResponse login(String email, String password);

    /**
     * 用户登出
     */
    void logout(String accessToken, String refreshToken);

    /**
     * 刷新token
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 修改密码并登出所有设备
     */
    void changePasswordAndLogoutAll(Long userId, String oldPassword, String newPassword);
}