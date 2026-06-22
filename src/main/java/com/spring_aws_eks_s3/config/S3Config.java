package com.spring_aws_eks_s3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String region;

    @Bean
    @Profile("dev")
    public S3Client localS3Client(
            @Value("${aws.credentials.access-key}") String accessKey,
            @Value("${aws.credentials.secret-key}") String secretKey) {
        
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Bean
    @Profile("prod")
    public S3Client testS3Client() {
        // Leverages WebIdentityTokenFileCredentialsProvider directly, 
        // which matches the token projection engineered by Pod Identity Webhooks
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .build();
    }
}
