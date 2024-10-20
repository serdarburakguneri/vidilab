package com.vidilab.ingestion.service;

import com.vidilab.ingestion.exception.FileStorageException;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class FileStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private static final String ERROR_MESSAGE_IO_FAILURE = "Failed to store file %s";
    private static final String FILE_PATH_FORMAT = "%s/%s";

    private final Executor fileStorageExecutor;

    public FileStorageService(Executor fileStorageExecutor) {
        this.fileStorageExecutor = fileStorageExecutor;
    }

    @Override
    public CompletableFuture<Void> storeFileAsync(String storagePath, MultipartFile file) {
        return CompletableFuture.runAsync(() -> {
            var fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            var filePath = Paths.get(FILE_PATH_FORMAT.formatted(storagePath, fileName));

            try {
                Files.createDirectories(filePath.getParent());

                try (var outputStream = Files.newOutputStream(filePath,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    var inputStream = file.getInputStream();
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                logger.debug("File {} stored successfully at {}", fileName, filePath);

            } catch (IOException ex) {
                logger.error("I/O error while storing file {}: {}", fileName, ex.getMessage());
                throw new FileStorageException(ERROR_MESSAGE_IO_FAILURE.formatted(fileName), ex);
            }
        }, fileStorageExecutor);
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (fileStorageExecutor instanceof ThreadPoolTaskExecutor) {
            ((ThreadPoolTaskExecutor) fileStorageExecutor).shutdown();
        }
    }
}
