package com.chunfeng.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {

    @Value("${spring.mail.username:}")
    private String sendUserName;

    @Value("${admin.emails:}")
    private String adminEmails;

    @Value("${project.folder}")
    private String projectFolder;


    /*
    * qq 登录相关
    * */
    @Value("${qq.app.id}")
    private String appId;
    @Value("${qq.app.key}")
    private String appKey;
    @Value("${qq.url.authorization}")
    private String authorizationUrl;
    @Value("${qq.url.access-token}")
    private String accessTokenUrl;
    @Value("${qq.url.openid}")
    private String openidUrl;
    @Value("${qq.url.user-info}")
    private String userInfoUrl;
    @Value("${qq.url.redirect}")
    private String redirectUrl;


}
