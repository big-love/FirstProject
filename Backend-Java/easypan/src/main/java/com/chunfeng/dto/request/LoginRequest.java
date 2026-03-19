package com.chunfeng.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @ClassName LoginRequest
 * @Author chunfeng
 * @Description
 * @date 2026/3/5 19:53
 * @Version 1.0
 */


/**
 * 登录请求数据传输对象
 *
 * 设计原则：
 * 1. 使用JSR-303注解进行参数校验
 * 2. 提供清晰的错误提示信息
 * 3. 字段命名遵循驼峰命名规范
 * 4. 添加Swagger文档注解便于API文档生成
 */
@Data
@Schema(description = "用户登录请求")
public class LoginRequest {

    /**
     * 用户邮箱
     *
     * 校验规则：
     * - 不能为空
     * - 必须符合邮箱格式
     * - 长度限制：5-100字符
     */
    @Schema(description = "用户邮箱", example = "user@example.com", required = true)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(min = 5, max = 100, message = "邮箱长度必须在5-100个字符之间")
    private String email;

    /**
     * 用户密码
     *
     * 校验规则：
     * - 不能为空
     * - 长度限制：6-50字符
     */
    @Schema(description = "用户密码", example = "password123", required = true)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    private String password;

    /**
     * 记住我标识（可选字段）
     *
     * 用途：
     * - 控制token过期时间
     * - true: 7天或30天
     * - false: 2小时或当天
     */
    @Schema(description = "是否记住登录状态", example = "false")
    private Boolean rememberMe = false;

    private String captchaKey;     // 图片验证码的 key
    private String captchaCode;    // 用户输入的验证码值
}