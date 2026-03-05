package com.chunfeng.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName SwaggerConfig
 * @Author chunfeng
 * @Description knife4j配置
 * @date 2026/3/3 09:33
 * @Version 1.0
 */
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("EasyPan")
                        .version("1.0")
                        .description("EasyPan API文档")
                        .termsOfService("http://www.easypan.com")
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("Apache 2.0")
                        )
                );
    }
}
