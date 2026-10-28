package com.notas.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteResponse(
        UUID id,
        String title,
        String body,
        String status,
        LocalDateTime createdAt
) {}
