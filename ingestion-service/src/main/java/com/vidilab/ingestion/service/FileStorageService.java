package com.vidilab.ingestion.service;

import com.vidilab.ingestion.exception.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class FileStorageService implements StorageService {

    private final String ERROR_MESSAGE_IO_FAILURE = "Failed to store file %s";
    private final String FILE_PATH_FORMAT = "%s/%s";

    @Override
    public CompletableFuture<Void> storeFileAsync(String storagePath, MultipartFile file) throws FileStorageException {
        var fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        var filePath = Path.of(FILE_PATH_FORMAT.formatted(storagePath, fileName));

        try {

            Files.createDirectories(filePath.getParent());

            try (var fileChannel = AsynchronousFileChannel.open(
                    filePath,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {
                ByteBuffer buffer = ByteBuffer.wrap(file.getBytes());

                var completableFuture = new CompletableFuture<Void>();

                fileChannel.write(buffer, 0, null, new CompletionHandler<>() {
                    @Override
                    public void completed(Integer result, Object attachment) {
                        completableFuture.complete(null);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        completableFuture.completeExceptionally(new FileStorageException(ERROR_MESSAGE_IO_FAILURE.formatted(fileName), exc));
                    }
                });

                return completableFuture;
            }

        } catch (IOException ex) {
            throw new FileStorageException(ERROR_MESSAGE_IO_FAILURE.formatted(fileName), ex);
        }
    }
}


