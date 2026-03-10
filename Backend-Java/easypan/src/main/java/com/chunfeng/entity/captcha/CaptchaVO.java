package com.chunfeng.entity.captcha;

import lombok.Data;

/**
 * @ClassName CaptchaVo
 * @Author chunfeng
 * @Description
 * @date 2026/3/6 10:09
 * @Version 1.0
 */
@Data
public class CaptchaVO {
    private String key;
    private String imageBase64;  // base64 格式，前端可直接 <img :src="imageBase64">
}