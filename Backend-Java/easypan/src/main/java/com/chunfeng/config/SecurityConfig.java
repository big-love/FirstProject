package com.chunfeng.config;


import com.chunfeng.componet.RedisComponent;
import com.chunfeng.security.JwtAuthFiler;
import com.chunfeng.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @ClassName SecurityConfig
 * @Author chunfeng
 * @Description  security安全拦截
 * @date 2025/10/23 09:39
 * @Version 1.0
 */

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           RedisComponent redisComponent,
                                           JwtUtil jwtUtil) throws Exception {
        JwtAuthFiler jwtAuthFiler = new JwtAuthFiler(redisComponent, jwtUtil);

        http
            .addFilterBefore(jwtAuthFiler, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // ✅ Swagger / Knife4j 文档
                        .requestMatchers(
                                "/doc.html",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/favicon.ico"

                        ).permitAll()

                        // ✅ 注册、验证码、登录接口 — 允许匿名访问
                    .requestMatchers(
                        "/api/auth/**"         // 如果有子路径也可以加
                    ).permitAll()

                        // ✅ 静态资源、错误页
                        .requestMatchers(
                                "/captcha/**",
                                "/error",
                                "/static/**"
                        ).permitAll()

                        // 🚫 其他接口都必须认证
                        .anyRequest().authenticated()
                )
                // 禁用 CSRF（Swagger 测试更方便）
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用默认登录页（防止跳 /login）
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
