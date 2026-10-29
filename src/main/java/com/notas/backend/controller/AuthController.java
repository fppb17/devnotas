package com.notas.backend.controller;

import com.notas.backend.dto.AuthResponse;
import com.notas.backend.dto.LoginRequest;
import com.notas.backend.dto.RegisterRequest;
import com.notas.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Endpoint protegido: solo accesible si envías un token JWT válido
    @GetMapping("/me")
    public Mono<Map<String, Object>> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> {
                    String email = (String) context.getAuthentication().getPrincipal();
                    return Map.of(
                            "email", email,
                            "message", "¡Éxito! Este es un endpoint protegido. Tu JWT funciona correctamente."
                    );
                });
    }
}