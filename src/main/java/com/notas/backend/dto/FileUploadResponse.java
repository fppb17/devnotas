package com.notas.backend.dto;

import java.time.LocalDateTime;

public record FileUploadResponse(
        String fileName,
        String fileUrl,
        String containerName,
        String mimeType,
        long sizeBytes,
        LocalDateTime uploadedAt
) {}