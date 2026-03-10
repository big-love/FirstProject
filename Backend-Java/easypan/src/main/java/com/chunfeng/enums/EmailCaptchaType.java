package com.chunfeng.enums;

import lombok.Getter;

/**
 * @ClassName EmailCaptchaType
 * @Author chunfeng
 * @Description 邮箱验证码业务类型
 * @date 2026/3/10 15:45
 * @Version 1.0
 */
@Getter
public enum EmailCaptchaType {

    REGISTER("register", "注册账号", "您正在注册账号，验证码有效期5分钟"),
    FORGET_PASSWORD("forget_password", "找回密码", "您正在找回密码，验证码有效期5分钟"),
    BIND_EMAIL("bind_email", "绑定邮箱", "您正在绑定新邮箱，验证码有效期5分钟"),
    VERIFY_EMAIL("verify_email", "验证邮箱", "您正在验证邮箱，验证码有效期5分钟"),
    LOGIN("login", "登录验证", "您正在登录，验证码有效期5分钟"),

    ;

    private final String code;
    private final String desc;
    private final String mailTips;

    EmailCaptchaType(String code, String desc, String mailTips) {
        this.code = code;
        this.desc = desc;
        this.mailTips = mailTips;
    }

    public static EmailCaptchaType fromCode(String code) {
        for (EmailCaptchaType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
