package com.vidilab.ingestion.service;

import com.vidilab.ingestion.exception.FileStorageException;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface StorageService {

    CompletableFuture<Void> storeFileAsync(String storagePath, MultipartFile file) throws FileStorageException;
}
