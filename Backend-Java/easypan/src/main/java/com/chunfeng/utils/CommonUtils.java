package com.chunfeng.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * @ClassName CommonUtils
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:32
 * @Version 1.0
 */
@Component
public class CommonUtils {

    /**
     * 从请求头中提取token
     * 支持 "Bearer token" 格式
     *
     * @param request HTTP请求
     * @return token字符串
     */
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}
