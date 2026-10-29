package com.notas.backend.dto;

public record AuthResponse(
        String token,
        String email,
        String name,
        String type
) {}
