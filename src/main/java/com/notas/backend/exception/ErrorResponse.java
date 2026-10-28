package com.notas.backend.exception;

import java.time.LocalDateTime;

// Este objeto define cómo se verán todos los errores de nuestra API
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {}
