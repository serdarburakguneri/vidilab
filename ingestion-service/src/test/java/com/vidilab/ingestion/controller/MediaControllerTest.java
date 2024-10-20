package com.vidilab.ingestion.controller;

import com.vidilab.ingestion.controller.constants.MediaControllerConstants;
import com.vidilab.ingestion.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MediaControllerTest {

    private MediaController mediaController;
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = Mockito.mock(FileStorageService.class);
        mediaController = new MediaController(fileStorageService);
    }

    @Test
    void testUploadMedia_Success() throws Exception {
        // Arrange
        var filePath = "/uploads/example.txt";
        var mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(fileStorageService.storeFileAsync(filePath, mockFile))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        var response = mediaController.uploadMedia(mockFile, filePath);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("File upload request received, processing.", response.getBody());
    }

    @Test
    void testUploadMedia_FileEmpty() {
        // Arrange
        var filePath = "/uploads/example.txt";
        var mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true);

        // Act
        var response = mediaController.uploadMedia(mockFile, filePath);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is empty.", response.getBody());
    }

    @Test
    void testUploadMedia_Exception() throws Exception {
        // Arrange
        var filePath = "/uploads/example.txt";
        var mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        // Simulate an exception when storing the file asynchronously
        when(fileStorageService.storeFileAsync(filePath, mockFile))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Error storing file")));

        // Act
        var response = mediaController.uploadMedia(mockFile, filePath);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(MediaControllerConstants.FILE_UPLOAD_REQUEST_RECEIVED, response.getBody());
    }
}
