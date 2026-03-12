package com.server.realsync.controllers;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.server.realsync.services.FileStorageService;


@RestController
@RequestMapping("/doc")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Value("${storage.use-cloud}")
    private boolean useCloud;
    
    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Real Sync scan file upload 
     * 
     * @param accountId
     * @param userId
     * @param file
     * @return
     */
    @PostMapping("/upload/a/{accountId}/p/{promptId}/{fileName}")
    public ResponseEntity<String> uploadPromptImage(
    		@PathVariable String accountId,
            @PathVariable String promptId,
            @PathVariable String fileName,
            @RequestParam("file") MultipartFile file) {
        String path = "a/" + accountId + "/p/" + promptId +"/";
        try {
            fileStorageService.uploadFile(path, file, fileName);
            return ResponseEntity.status(HttpStatus.OK).body("Prompt Image uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file.");
        }
    }
    
    private String generateETag(String filename) {
        // Generate a simple ETag based on the filename or a hash of the file
        // For more robust solutions, consider using file hashes or last modified timestamps
        return Integer.toHexString(filename.hashCode());
    }
    
    @GetMapping("/get/a/{accountId}/p/{promptId}/{fileName}")
    public ResponseEntity<InputStreamResource> getLegalResearchFile(
            @PathVariable String accountId,
            @PathVariable String fileName,
            @PathVariable String promptId) {
    	String path = "a/" + accountId + "/p/" + promptId +"/";
        try {
            InputStream inputStream = fileStorageService.downloadFile(path, fileName);
            // Set caching headers
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + promptId + "\"");
            headers.set(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000"); // Cache for 1 year
            headers.set(HttpHeaders.ETAG, generateETag(promptId)); // Generate or retrieve ETag
            //headers.set(HttpHeaders.CONTENT_TYPE, "image/jpeg"); // Adjust based on your image type
            return ResponseEntity.ok()
            		.headers(headers)
                    .body(new InputStreamResource(inputStream));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
}
