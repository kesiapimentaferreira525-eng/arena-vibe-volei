package arena_vibe_volei.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI arenaVibeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Arena Vibe Sports API")
                        .version("v1")
                        .description("API para cadastro de quadras, reservas, pagamentos e disponibilidade."));
    }
}
