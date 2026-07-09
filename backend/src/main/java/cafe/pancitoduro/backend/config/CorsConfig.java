package cafe.pancitoduro.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ORÍGENES PERMITIDOS
        config.setAllowedOrigins(
                List.of(
                        // FRONTEND LOCAL
                        "http://127.0.0.1:5500",

                        // OPCIONAL: por si usas localhost
                        "http://localhost:5500",

                        // FRONTEND PRODUCCIÓN
                        "https://pancitoduro.cafe",
                        "https://www.pancitoduro.cafe"
                )
        );

        // MÉTODOS HTTP PERMITIDOS
        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // HEADERS QUE PUEDE ENVIAR EL FRONTEND
        config.setAllowedHeaders(
                List.of("*")
        );

        // HEADERS QUE EL FRONTEND PUEDE LEER
        config.setExposedHeaders(
                List.of(
                        "Set-Cookie"
                )
        );

        // NECESARIO PARA ENVIAR JSESSIONID
        config.setAllowCredentials(true);

        // CACHE DEL PREFLIGHT CORS
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }
}