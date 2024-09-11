package org.roadmap.filedrive.repository;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.roadmap.filedrive.utils.MinioProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MinioClientBuilder {

    private final MinioProperties properties;

    @Bean
    public MinioClient buildMinioClient() throws MinioUnknownException {
        var minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey()).build();
        createDefaultBucketIfNotExist(minioClient);
        return minioClient;
    }

    private void createDefaultBucketIfNotExist(MinioClient minioClient) throws MinioUnknownException {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getDefaultBucket()).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getDefaultBucket()).build());
            }
        } catch (Exception e) {
            throw new MinioUnknownException(e);
        }
    }
}
