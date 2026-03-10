package com.chunfeng.service.captcha;

import com.chunfeng.dto.request.EmailCaptchaRequest;
import com.chunfeng.dto.request.RegisterRequest;
import com.chunfeng.dto.response.LoginResponse;
import com.chunfeng.entity.captcha.CaptchaVO;

/**
 * @ClassName CaptchaService
 * @Author chunfeng
 * @Description
 * @date 2026/3/6 10:08
 * @Version 1.0
 */
public interface CaptchaService {

    /**
     * 生成并保存邮箱验证码（6位数字），同时返回code供发邮件使用
     * @return 验证码（明文，仅用于发邮件）
     */
    void generateEmailCaptcha(EmailCaptchaRequest request);

    /**
     * 生成图片验证码（4位，字母+数字，大小写不敏感）
     */
    CaptchaVO generateImageCaptcha();

    /**
     * 校验图片验证码（大小写不敏感）
     */
    void verifyImageCaptcha(String key, String code);

    /**
     * 校验邮箱验证码
     */
    void verifyEmailCaptcha(String email, String type, String code);

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
     * 用户注册
     */
    LoginResponse register(RegisterRequest request);
}
