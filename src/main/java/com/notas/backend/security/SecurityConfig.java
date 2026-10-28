package com.notas.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Desactivar CSRF (necesario para APIs REST con Postman/React)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Configurar qué rutas requieren autenticación
                .authorizeExchange(exchanges -> exchanges
                        // Estas rutas son públicas (no requieren login)
                        .pathMatchers("/api/v1/health/**").permitAll()
                        .pathMatchers("/api/v1/notes/**").permitAll()
                        .pathMatchers("/api/v1/files/**").permitAll()

                        // Todas las demás rutas requieren autenticación
                        .anyExchange().authenticated()
                )

                // Desactivar el formulario de login por defecto
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                // Desactivar el logout por defecto
                .logout(ServerHttpSecurity.LogoutSpec::disable)

                .build();
    }
}
