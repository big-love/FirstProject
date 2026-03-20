package com.chunfeng.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SwaggerConfig
 * @Author chunfeng
 * @Description knife4j 配置
 * @date 2026/3/3 09:33
 * @Version 1.0
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EasyPan")
                        .version("1.0")
                        .description("EasyPan API 文档")
                        .termsOfService("http://www.easypan.com")
                        .license(new License()
                                .name("Apache 2.0")
                        )
                );
    }
}
