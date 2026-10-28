package com.notas.backend.controller;

import com.notas.backend.dto.FileUploadResponse;
import com.notas.backend.service.FileStorageService;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FileUploadResponse> uploadMedia(ServerWebExchange exchange) {
        return handleFileUpload(exchange, fileStorageService::uploadToPublicMedia);
    }

    @PostMapping(value = "/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FileUploadResponse> uploadProfile(ServerWebExchange exchange) {
        return handleFileUpload(exchange, fileStorageService::uploadToPublicProfiles);
    }

    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FileUploadResponse> uploadPrivate(ServerWebExchange exchange) {
        return handleFileUpload(exchange, fileStorageService::uploadToPrivateUploads);
    }

    private Mono<FileUploadResponse> handleFileUpload(
            ServerWebExchange exchange,
            FileUploadHandler handler) {

        return exchange.getMultipartData()
                .flatMap(multipartData -> {
                    var part = multipartData.getFirst("file");
                    if (part == null) {
                        return Mono.error(new IllegalArgumentException("No se encontró el archivo"));
                    }

                    if (part instanceof org.springframework.http.codec.multipart.FilePart filePart) {
                        return DataBufferUtils.join(filePart.content())
                                .map(dataBuffer -> {
                                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(bytes);
                                    DataBufferUtils.release(dataBuffer);
                                    return bytes;
                                })
                                .flatMap(bytes -> handler.upload(
                                        bytes,
                                        filePart.filename(),
                                        filePart.headers().getContentType() != null
                                                ? filePart.headers().getContentType().toString()
                                                : "application/octet-stream"
                                ));
                    }

                    return Mono.error(new IllegalArgumentException("Parte no válida"));
                });
    }

    @FunctionalInterface
    interface FileUploadHandler {
        Mono<FileUploadResponse> upload(byte[] bytes, String fileName, String contentType);
    }
}