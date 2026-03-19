package com.chunfeng.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @ClassName RegisterRequest
 * @Author chunfeng
 * @Description
 * @date 2026/3/10 16:33
 * @Version 1.0
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须是6位")
    private String emailCode;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度应在8-32位之间")
    private String registerPassword;

    @NotBlank(message = "确认密码不能为空")
    private String reRegisterPassword;

    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 16, message = "昵称长度应在2-16位之间")
    private String nickName;

    private String captchaKey;     // 图片验证码的 key
    private String captchaCode;    // 用户输入的验证码值
}
