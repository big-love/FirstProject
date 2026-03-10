package com.chunfeng.service.impl;


import com.chunfeng.dto.response.LoginResponse;
import com.chunfeng.entity.po.UserInfo;
import com.chunfeng.exception.BusinessException;
import com.chunfeng.mapper.UserMapper;
import com.chunfeng.security.JwtUtil;
import com.chunfeng.service.UserService;
import com.chunfeng.service.redis.TokenRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 *
 * @author chunfeng
 * @date 2026/3/5
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    private TokenRedisService tokenRedisService;


    /**
     * 修改密码后强制登出所有设备
     *
     * @param userId 用户ID
     */
    @Override
    public void changePasswordAndLogoutAll(Long userId, String oldPassword, String newPassword) {
        // 1. 查询用户
        UserInfo userInfo = userMapper.selectById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }

        // 2. 验证旧密码
        if (!bCryptPasswordEncoder.matches(oldPassword, userInfo.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 3. 更新密码
        String encodedNewPassword = bCryptPasswordEncoder.encode(newPassword);
        userInfo.setPassword(encodedNewPassword);
        userMapper.updateById(userInfo);

        // 4. 删除该用户的所有token（强制所有设备重新登录）
        tokenRedisService.deleteAllUserTokens(userId);
    }
}