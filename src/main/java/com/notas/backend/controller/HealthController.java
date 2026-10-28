package com.notas.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public Mono<Map<String, Object>> checkHealth() {
        return Mono.just(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "message", "Backend funcionando en modo local (sin BD)"
        ));
    }
}
