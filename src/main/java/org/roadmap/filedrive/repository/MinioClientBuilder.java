package org.roadmap.filedrive.repository;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Getter;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioClientBuilder {

    @Value("${minio.default.bucket}")
    @Getter
    private String defaultBucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;


    @Bean
    public MinioClient buildMinioClient() throws MinioUnknownException {
        var minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        createDefaultBucketIfNotExist(minioClient);
        return minioClient;
    }

    private void createDefaultBucketIfNotExist(MinioClient minioClient) throws MinioUnknownException {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(defaultBucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(defaultBucket).build());
            }
        } catch (Exception e) {
            throw new MinioUnknownException(e);
        }
    }
}
