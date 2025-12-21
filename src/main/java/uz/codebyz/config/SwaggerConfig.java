package uz.codebyz.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .addServersItem(
                        new io.swagger.v3.oas.models.servers.Server()
                                .url("http://164.90.174.138:8094")
//                                .url("https://codebyz.online")
                                .description("Production server")
                )
                .info(new Info()
                                .title("CodeByZ Platform API")
                                .description("CodeByZ Online Course Platform REST API hujjatlari")
                                .version("1.0.0")
                                .contact(new Contact()
                                                .name("Zafar Ziyatov")
                                                .email("ziyatovzafar98@gmail.com")
//                                .url("https://codebyz.online")
                                                .url("http://164.90.174.138:8094")
                                )
                )
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(
                        new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes(
                                        "BearerAuth",
                                        new SecurityScheme()
                                                .name("Authorization")
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}
