package com.vidilab.ingestion.controller;

import com.vidilab.ingestion.controller.constants.MediaControllerConstants;
import com.vidilab.ingestion.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);

    private final FileStorageService fileStorageService;

    public MediaController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadMedia(@RequestParam("file") MultipartFile file,
                                              @RequestParam("filePath") String filePath) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MediaControllerConstants.FILE_IS_EMPTY);
        }

        fileStorageService.storeFileAsync(filePath, file)
                .thenAccept(result -> logger.debug("File successfully stored: {}", file.getOriginalFilename()))
                .exceptionally(ex -> {
                    logger.error("Failed to store file: {}", file.getOriginalFilename(), ex);
                    return null;
                });

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(MediaControllerConstants.FILE_UPLOAD_REQUEST_RECEIVED);
    }
}
