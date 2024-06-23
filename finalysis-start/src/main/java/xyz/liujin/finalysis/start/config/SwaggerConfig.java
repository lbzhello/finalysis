package xyz.liujin.finalysis.start.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 访问地址
 * http://localhost:8888/doc.html
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI createRestApi() {
        return new OpenAPI().info(new Info().title("接口文档标题")
                        .description("描述")
                        .version("v1"))
                .externalDocs(new ExternalDocumentation()
                        .description("项目 API 文档")
                        .url("/"));
    }

}
