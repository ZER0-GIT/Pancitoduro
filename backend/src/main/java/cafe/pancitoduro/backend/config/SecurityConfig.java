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

                        // AUTENTICACIÓN Y SESIÓN MANUAL

                        .requestMatchers(
                                "/api/auth/registro",
                                "/api/auth/login",
                                "/api/auth/me",
                                "/api/auth/logout"
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


                        // CREAR PEDIDO
                        // PedidoController valida usuarioId en HttpSession

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/pedidos"
                        )
                        .permitAll()


                        // CONSULTAR PEDIDOS PROPIOS
                        // PedidoController valida usuarioId en HttpSession

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/pedidos/mis-pedidos",
                                "/api/pedidos/mis-pedidos/**"
                        )
                        .permitAll()


                        // ADMINISTRACIÓN CATEGORÍAS

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categorias/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categorias/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/categorias/**"
                        )
                        .hasRole("ADMIN")


                        // ADMINISTRACIÓN PRODUCTOS

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/productos/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/productos/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/productos/**"
                        )
                        .hasRole("ADMIN")


                        // ADMINISTRACIÓN OFERTAS

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/ofertas/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/ofertas/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/ofertas/**"
                        )
                        .hasRole("ADMIN")


                        // LISTAR TODOS LOS PEDIDOS

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/pedidos"
                        )
                        .hasRole("ADMIN")


                        // CUALQUIER OTRO ENDPOINT

                        .anyRequest()
                        .authenticated()
                );

        return http.build();
    }
}