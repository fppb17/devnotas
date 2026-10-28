package com.notas.backend.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.notas.backend.dto.FileUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileStorageService {

    private final BlobServiceClient blobServiceClient;

    @Value("${azure.storage.container.public-media}")
    private String publicMediaContainer;

    @Value("${azure.storage.container.public-profiles}")
    private String publicProfilesContainer;

    @Value("${azure.storage.container.private-uploads}")
    private String privateUploadsContainer;

    public FileStorageService(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    public Mono<FileUploadResponse> uploadFile(byte[] fileBytes, String fileName, String contentType, String containerName) {
        return Mono.fromCallable(() -> {
                    if (fileBytes == null || fileBytes.length == 0) {
                        throw new IllegalArgumentException("El archivo está vacío");
                    }

                    String extension = fileName != null && fileName.contains(".")
                            ? fileName.substring(fileName.lastIndexOf("."))
                            : "";
                    String uniqueFilename = UUID.randomUUID().toString() + extension;

                    BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
                    BlobClient blobClient = containerClient.getBlobClient(uniqueFilename);

                    BlobHttpHeaders headers = new BlobHttpHeaders()
                            .setContentType(contentType);

                    blobClient.upload(
                            new ByteArrayInputStream(fileBytes),
                            fileBytes.length,
                            true
                    );

                    blobClient.setHttpHeaders(headers);

                    String fileUrl = blobClient.getBlobUrl();

                    return new FileUploadResponse(
                            uniqueFilename,
                            fileUrl,
                            containerName,
                            contentType,
                            fileBytes.length,
                            LocalDateTime.now()
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<FileUploadResponse> uploadToPublicMedia(byte[] fileBytes, String fileName, String contentType) {
        return uploadFile(fileBytes, fileName, contentType, publicMediaContainer);
    }

    public Mono<FileUploadResponse> uploadToPublicProfiles(byte[] fileBytes, String fileName, String contentType) {
        return uploadFile(fileBytes, fileName, contentType, publicProfilesContainer);
    }

    public Mono<FileUploadResponse> uploadToPrivateUploads(byte[] fileBytes, String fileName, String contentType) {
        return uploadFile(fileBytes, fileName, contentType, privateUploadsContainer);
    }
}