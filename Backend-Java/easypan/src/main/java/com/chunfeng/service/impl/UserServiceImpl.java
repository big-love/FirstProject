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
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRedisService tokenRedisService;

    @Override
    public LoginResponse login(String email, String password) {
        // 1. 查询用户信息
        UserInfo userInfo = userMapper.selectByEmail(email);

        // 2. 判断用户是否存在
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }

        // 3. 判断密码是否正确
        if (!bCryptPasswordEncoder.matches(password, userInfo.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 4. 账号状态检查
        if (userInfo.getStatus() == 0) {
            throw new BusinessException("账号被禁用");
        }

        // 5. 组装返回LoginResponse（包含双token生成和Redis存储）
        return assembleLoginResponse(userInfo);
    }

    /**
     * 组装登录响应
     * 生成双token并保存到Redis
     *
     * @param userInfo 用户信息PO
     * @return LoginResponse 登录响应DTO
     */
    private LoginResponse assembleLoginResponse(UserInfo userInfo) {
        // 1. 生成双token
        String accessToken = jwtUtil.generateAccessToken(userInfo.getUserId());
        String refreshToken = jwtUtil.generateRefreshToken(userInfo.getUserId());

        // 2. 获取过期时间（毫秒）
        long now = System.currentTimeMillis();
        long accessExpireTime = now + jwtUtil.getAccessTokenExpireDuration();
        long refreshExpireTime = now + jwtUtil.getRefreshTokenExpireDuration();

        // 3. 计算Redis存储的过期时长
        // AccessToken: 转换为分钟（通常30分钟）
        long accessExpireMinutes = jwtUtil.getAccessTokenExpireDuration() / (1000 * 60);
        // RefreshToken: 转换为天（通常7天）
        long refreshExpireDays = jwtUtil.getRefreshTokenExpireDuration() / (1000 * 60 * 60 * 24);

        // 4. 保存token到Redis（核心步骤）
        tokenRedisService.saveAccessToken(accessToken, userInfo.getUserId(), accessExpireMinutes);
        tokenRedisService.saveRefreshToken(refreshToken, userInfo.getUserId(), refreshExpireDays);

        // 5. 构建并返回登录响应
        return new LoginResponse()
            .setUserId(userInfo.getUserId())
            .setNickName(userInfo.getNickName())
            .setAvatar(userInfo.getQqAvatar())
            .setAccessToken(accessToken)
            .setRefreshToken(refreshToken)
            .setAccessTokenExpireTime(accessExpireTime)
            .setRefreshTokenExpireTime(refreshExpireTime)
            .setEmail(userInfo.getEmail());
    }

    /**
     * 用户登出
     * 删除Redis中的token
     *
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     */
    @Override
    public void logout(String accessToken, String refreshToken) {
        // 删除双token
        tokenRedisService.deleteAccessToken(accessToken);
        tokenRedisService.deleteRefreshToken(refreshToken);
    }

    /**
     * 刷新AccessToken
     * 使用RefreshToken获取新的AccessToken
     *
     * @param refreshToken 刷新令牌
     * @return 新的登录响应（包含新的AccessToken）
     */
    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // 1. 验证RefreshToken并获取userId
        Long userId = tokenRedisService.validateRefreshToken(refreshToken);
        if (userId == null) {
            throw new BusinessException("RefreshToken无效或已过期，请重新登录");
        }

        // 2. 查询用户信息（确保用户状态正常）
        UserInfo userInfo = userMapper.selectById(userId);
        if (userInfo == null) {
            throw new BusinessException("用户不存在");
        }
        if (userInfo.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 3. 生成新的AccessToken
        String newAccessToken = jwtUtil.generateAccessToken(userId);
        long accessExpireMinutes = jwtUtil.getAccessTokenExpireDuration() / (1000 * 60);

        // 4. 保存新的AccessToken到Redis
        tokenRedisService.saveAccessToken(newAccessToken, userId, accessExpireMinutes);

        // 5. 返回新的token信息
        long now = System.currentTimeMillis();
        return new LoginResponse()
            .setUserId(userInfo.getUserId())
            .setNickName(userInfo.getNickName())
            .setAvatar(userInfo.getQqAvatar())
            .setAccessToken(newAccessToken)
            .setRefreshToken(refreshToken)  // RefreshToken保持不变
            .setAccessTokenExpireTime(now + jwtUtil.getAccessTokenExpireDuration())
            .setRefreshTokenExpireTime(now + jwtUtil.getRefreshTokenExpireDuration())
            .setEmail(userInfo.getEmail());
    }

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