package com.sarkar.kafka.stream.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Value("${kafka.openapi.dev-url}")
    private String localUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl(localUrl);
        localServer.setDescription("Server URL in localhost");

        Info info = new Info()
                .title("Kafka API")
                .version("1.0")
                .description("This Kafka Stream.");
        return new OpenAPI().info(info).servers(List.of(localServer));
    }
}
