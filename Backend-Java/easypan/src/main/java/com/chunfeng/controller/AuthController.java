package com.chunfeng.controller;

import com.chunfeng.common.Result;
import com.chunfeng.dto.request.LoginRequest;
import com.chunfeng.dto.request.RefreshTokenRequest;
import com.chunfeng.dto.response.LoginResponse;
import com.chunfeng.security.JwtUtil;
import com.chunfeng.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 认证控制器
 * 处理登录、登出、token刷新等认证相关操作
 *
 * @author chunfeng
 * @date 2026/3/5
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     *
     * @param request 登录请求（邮箱+密码）
     * @return 登录响应（包含双token和用户信息）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getEmail(), request.getPassword());
        return Result.success(response, "登录成功");
    }

    /**
     * 用户登出
     * 从请求头获取token并删除
     *
     * @param httpRequest HTTP请求对象
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest httpRequest) {
        // 从请求头获取AccessToken
        String accessToken = extractToken(httpRequest);

        // 从请求头或参数获取RefreshToken（可选）
        String refreshToken = httpRequest.getHeader("X-Refresh-Token");

        // 执行登出
        userService.logout(accessToken, refreshToken);

        return Result.success("登出成功");
    }

    /**
     * 刷新AccessToken
     * 使用RefreshToken获取新的AccessToken
     *
     * @param request 刷新token请求
     * @return 新的登录响应（包含新的AccessToken）
     */
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@Validated @RequestBody RefreshTokenRequest request) {
        LoginResponse response = userService.refreshToken(request.getRefreshToken());
        return Result.success(response, "Token刷新成功");
    }

//    /**
//     * 修改密码
//     * 修改成功后会强制登出所有设备
//     *
//     * @param request 修改密码请求
//     * @param httpRequest HTTP请求对象
//     * @return 操作结果
//     */
//    @PostMapping("/change-password")
//    public Result<Void> changePassword(
//        @Validated @RequestBody ChangePasswordRequest request,
//        HttpServletRequest httpRequest) {
//
//        // 从token中获取当前用户ID
//        String accessToken = extractToken(httpRequest);
//        Long userId = jwtUtil.getUserIdFromAccessToken(accessToken);
//
//        // 修改密码并登出所有设备
//        userService.changePasswordAndLogoutAll(userId, request.getOldPassword(), request.getNewPassword());
//
//        return Result.success("密码修改成功，请重新登录");
//    }

    /**
     * 验证token是否有效
     * 用于前端检查登录状态
     *
     * @param httpRequest HTTP请求对象
     * @return 验证结果
     */
    @GetMapping("/validate")
    public Result<Boolean> validateToken(HttpServletRequest httpRequest) {
        try {
            String accessToken = extractToken(httpRequest);
            Long userId = jwtUtil.getUserIdFromAccessToken(accessToken);
            return Result.success(userId != null, "Token有效");
        } catch (Exception e) {
            return Result.success(false, "Token无效");
        }
    }

    /**
     * 从请求头中提取token
     * 支持 "Bearer token" 格式
     *
     * @param request HTTP请求
     * @return token字符串
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}