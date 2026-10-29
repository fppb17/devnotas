package com.notas.backend.service;

import com.notas.backend.dto.AuthResponse;
import com.notas.backend.dto.LoginRequest;
import com.notas.backend.dto.RegisterRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    // Base de datos simulada en memoria (Thread-safe)
    private final Map<String, UserMock> users = new ConcurrentHashMap<>();

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();

        // Precargamos un usuario de prueba
        String encodedPassword = passwordEncoder.encode("123456");
        users.put("test@notas.com", new UserMock(
                UUID.randomUUID(),
                "Usuario Test",
                "test@notas.com",
                encodedPassword
        ));
    }

    public Mono<AuthResponse> register(RegisterRequest request) {
        return Mono.fromCallable(() -> {
            if (users.containsKey(request.email())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }

            String encodedPassword = passwordEncoder.encode(request.password());
            UserMock user = new UserMock(
                    UUID.randomUUID(),
                    request.name(),
                    request.email(),
                    encodedPassword
            );
            users.put(request.email(), user);

            String token = jwtService.generateToken(user.email(), user.name());
            return new AuthResponse(token, user.email(), user.name(), "Bearer");
        }).subscribeOn(Schedulers.boundedElastic()); // BCrypt es bloqueante, lo movemos a un hilo elástico
    }

    public Mono<AuthResponse> login(LoginRequest request) {
        return Mono.fromCallable(() -> {
            UserMock user = users.get(request.email());

            if (user == null || !passwordEncoder.matches(request.password(), user.password())) {
                throw new IllegalArgumentException("Credenciales inválidas");
            }

            String token = jwtService.generateToken(user.email(), user.name());
            return new AuthResponse(token, user.email(), user.name(), "Bearer");
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // Clase interna para simular la entidad de Usuario
    public record UserMock(
            UUID id,
            String name,
            String email,
            String password
    ) {}
}
