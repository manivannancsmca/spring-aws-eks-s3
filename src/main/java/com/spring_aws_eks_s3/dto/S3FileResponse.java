package com.spring_aws_eks_s3.dto;

import java.time.Instant;

public record S3FileResponse(
    String filename,
    long sizeInBytes,
    Instant lastModified
) {}