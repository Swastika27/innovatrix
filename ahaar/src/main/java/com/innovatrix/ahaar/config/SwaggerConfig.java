package com.innovatrix.ahaar.config;


import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myConfig(){
        return new OpenAPI()
                .info(
                new Info().title("Ahaar App API")
                        .description("Created by Innovatrix")
                )
                .servers(
                        List.of( new Server().url("http://localhost:8080").description("local"),
                                 new Server().url("http://localhost:8082").description("dummy"))

                        );
    }
}
