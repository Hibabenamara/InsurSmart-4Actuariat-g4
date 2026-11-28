package tn.esprit.contratservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI contractOpenApi(@Value("${server.port:8082}") String port) {
        return new OpenAPI()
                .info(new Info()
                        .title("Contract Microservice API")
                        .version("1.0.0")
                        .description("REST API to manage insurance types, guarantees, subscription requests " +
                                "and contracts. Identity via headers X-User-Id / X-User-Role (CLIENT, AGENT, ADMIN).")
                        .contact(new Contact().name("Layla Jendoubi")))
                .servers(List.of(new Server().url("http://localhost:" + port).description("Local")));
    }
}