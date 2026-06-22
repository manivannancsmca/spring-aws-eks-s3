package com.spring_aws_eks_s3.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.spring_aws_eks_s3.dto.S3FileResponse;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageService(S3Client s3Client, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void storePayload(String filename, String content) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .contentType("text/plain")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content.getBytes(StandardCharsets.UTF_8)));
    }

    public List<S3FileResponse> listFiles() {
        // 1. Build the initial list request targeting the bucket
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        // 2. Obtain an Iterable paginator from the S3Client
        ListObjectsV2Iterable pages = s3Client.listObjectsV2Paginator(listRequest);

        // 3. Process the response stream, flattening page blocks into individual
        // elements
        return pages.stream()
                .flatMap(page -> page.contents().stream())
                .map(s3Object -> new S3FileResponse(
                        s3Object.key(),
                        s3Object.size(),
                        s3Object.lastModified()))
                .collect(Collectors.toList());
    }

}