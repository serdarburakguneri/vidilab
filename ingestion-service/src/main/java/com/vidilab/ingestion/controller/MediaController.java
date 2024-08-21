package com.vidilab.ingestion.controller;

import com.vidilab.ingestion.exception.FileStorageException;
import com.vidilab.ingestion.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final FileStorageService fileStorageService;

    public MediaController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadMedia(@RequestParam("file") MultipartFile file,
                                              @RequestParam("filePath") String filePath) throws FileStorageException {
        fileStorageService.storeFileAsync(filePath, file)
                .thenAccept(result -> {

                })
                .exceptionally(ex -> {
                    return null;
                });

        return ResponseEntity.ok("File upload request received");
    }
}

