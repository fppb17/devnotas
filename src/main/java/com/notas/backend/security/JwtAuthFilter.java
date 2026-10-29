package com.notas.backend.security;

import com.notas.backend.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements WebFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 1. Verificar si existe el header y empieza con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 2. Validar el token
            if (jwtService.validateToken(token)) {
                String email = jwtService.getEmailFromToken(token);

                // 3. Crear el objeto de autenticación de Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email, // Principal (el email del usuario)
                                null,  // Credentials (no las necesitamos después del login)
                                List.of(new SimpleGrantedAuthority("ROLE_USER")) // Roles
                        );

                SecurityContext context = new SecurityContextImpl(authentication);

                // 4. Inyectar el contexto en la cadena reactiva
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
            }
        }

        // Si no hay token o no es válido, simplemente continúa (SecurityConfig decidirá si bloquear o no)
        return chain.filter(exchange);
    }
}
