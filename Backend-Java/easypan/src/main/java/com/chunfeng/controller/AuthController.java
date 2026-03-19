package com.chunfeng.controller;

import cn.hutool.core.util.StrUtil;
import com.chunfeng.common.Result;
import com.chunfeng.dto.request.EmailCaptchaRequest;
import com.chunfeng.dto.request.LoginRequest;
import com.chunfeng.dto.request.RefreshTokenRequest;
import com.chunfeng.dto.request.RegisterRequest;
import com.chunfeng.dto.response.LoginResponse;
import com.chunfeng.entity.captcha.CaptchaVO;
import com.chunfeng.entity.constants.Constants;
import com.chunfeng.security.JwtUtil;
import com.chunfeng.service.captcha.CaptchaService;
import com.chunfeng.utils.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 认证控制器
 * 处理注册, 登录、登出、token刷新等认证相关操作
 *
 * @author chunfeng
 * @date 2026/3/5
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CommonUtils commonUtils;


    /*
     * 获取图片验证码
     *
     * @return 图片验证码(Base64)
     * */
    @PostMapping("/checkCode")
    public Result<CaptchaVO> getImageCaptcha() {
        return Result.success(captchaService.generateImageCaptcha());
    }



    /*
     * 发送邮箱验证码
     *
     * */
    @PostMapping("/sendEmailCode")
    public Result<String> sendEmailCode(@Validated @RequestBody EmailCaptchaRequest request) {
        // 1.图像验证码是否为空
        if (StrUtil.isBlank(request.getImageKey()) || StrUtil.isBlank(request.getImageCode())) {
            return Result.fail("请输入图片验证码");
        }
        // 2.检验图片验证码
        try {
            captchaService.verifyImageCaptcha(request.getImageKey(), request.getImageCode());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        // 3.发送邮箱验证码
        captchaService.generateEmailCaptcha(request);
        return Result.success("验证码已发送，请查收(有效期" + Constants.EMAIL_EXPIRE_MINUTES + ")分钟");

    }


    /**
     * 用户登录
     *
     * @param request 登录请求（邮箱+密码）
     * @return 登录响应（包含双token和用户信息）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        //图片验证码是否为空
        if (StrUtil.isBlank(request.getCaptchaKey()) || StrUtil.isBlank(request.getCaptchaCode())) {
            return Result.fail("请输入图片验证码");
        }
        try {
            // 检验图片验证码
            captchaService.verifyImageCaptcha(request.getCaptchaKey(), request.getCaptchaCode());
        } catch (Exception e){
            return Result.fail(e.getMessage());
        }

        LoginResponse response = captchaService.login(request.getEmail(), request.getPassword());
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
        String accessToken = commonUtils.extractToken(httpRequest);

        // 从请求头或参数获取RefreshToken（可选）
        String refreshToken = httpRequest.getHeader("X-Refresh-Token");

        // 执行登出
        captchaService.logout(accessToken, refreshToken);

        return Result.success("登出成功");
    }


    /*
    * 注册
    *
    * */
    @PostMapping("/register")
    public Result<LoginResponse> register(@Validated @RequestBody RegisterRequest request) {
        return Result.success(captchaService.register(request), "注册成功");
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
        LoginResponse response = captchaService.refreshToken(request.getRefreshToken());
        return Result.success(response, "Token刷新成功");
    }

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
            String accessToken = commonUtils.extractToken(httpRequest);
            Long userId = jwtUtil.getUserIdFromAccessToken(accessToken);
            return Result.success(userId != null, "Token有效");
        } catch (Exception e) {
            return Result.success(false, "Token无效");
        }
    }



}