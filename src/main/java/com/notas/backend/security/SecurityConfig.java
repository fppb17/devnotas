package com.notas.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.SecurityContextServerWebExchangeWebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Desactivar CSRF (necesario para APIs REST)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Configurar rutas públicas y protegidas
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/health/**").permitAll()
                        .pathMatchers("/api/v1/auth/**").permitAll() // Login y registro son públicos
                        .pathMatchers("/api/v1/notes/**").permitAll() // Mantenemos notas públicas por ahora
                        .pathMatchers("/api/v1/files/**").permitAll() // Mantenemos archivos públicos por ahora
                        .anyExchange().authenticated() // Todo lo demás requiere autenticación
                )

                // Agregar nuestro filtro JWT antes del filtro de contexto de seguridad
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.SECURITY_CONTEXT_SERVER_WEB_EXCHANGE)

                // Desactivar el formulario de login por defecto de Spring
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .build();
    }
}
