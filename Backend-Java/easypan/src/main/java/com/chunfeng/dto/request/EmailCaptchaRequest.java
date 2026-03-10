package com.chunfeng.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @ClassName EmailCaptchaRequest
 * @Author chunfeng
 * @Description
 * @date 2026/3/6 10:10
 * @Version 1.0
 */
@Data
public class EmailCaptchaRequest {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    private String email;

    @NotBlank(message = "业务类型不能为空")
    private String type;  // register / forget_password / verify_email

    private Boolean needImageCaptcha = false;
    private String imageKey;
    private String imageCode;

    private String captchaKey;     // 图片验证码的 key
    private String captchaCode;    // 用户输入的验证码值
}
