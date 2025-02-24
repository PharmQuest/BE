package com.pharmquest.pharmquest.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");

        List<Server> servers = new ArrayList<>();
        if (isLocalEnvironment()) {
            servers.add(new Server().url("http://localhost:8080").description("Local Server"));
        } else {
            servers.add(new Server().url("https://api.pharmquest.store").description("Production API Server"));
        }

        return new OpenAPI()
                .info(new Info()
                        .title("PharmQuest API")
                        .version("1.0")
                        .description("PharmQuest 프로젝트 API 문서"))
                .servers(servers)
                .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .addSecurityItem(securityRequirement);
    }

    private boolean isLocalEnvironment() {
        String env = System.getProperty("spring.profiles.active", "local");
        return "local".equals(env);
    }

}
