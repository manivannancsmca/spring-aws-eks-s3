package com.spring_aws_eks_s3.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring_aws_eks_s3.service.S3StorageService;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final S3StorageService s3StorageService;

    public StorageController(S3StorageService s3StorageService) {
        this.s3StorageService = s3StorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam String filename, 
            @RequestBody String content) {
        
        // This is exactly how you call your function!
        s3StorageService.storePayload(filename, content);
        
        return ResponseEntity.ok("File uploaded successfully to S3!");
    }
}
