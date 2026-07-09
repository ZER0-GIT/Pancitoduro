package cafe.pancitoduro.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // AUTENTICACIÓN

                        .requestMatchers(
                                "/api/auth/**"
                        )
                        .permitAll()


                        // CATÁLOGO PÚBLICO

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categorias/**",
                                "/api/productos/**",
                                "/api/ofertas/**"
                        )
                        .permitAll()


                        // PEDIDOS

                        .requestMatchers(
                                "/api/pedidos/**"
                        )
                        .permitAll()


                        // ADMINISTRACIÓN
                        // Los controladores validarán manualmente
                        // usuarioId y rol ADMIN.

                        .requestMatchers(
                                "/api/categorias/**",
                                "/api/productos/**",
                                "/api/ofertas/**"
                        )
                        .permitAll()


                        // CUALQUIER OTRO ENDPOINT

                        .anyRequest()
                        .authenticated()
                );

        return http.build();
    }
}