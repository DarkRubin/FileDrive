package org.roadmap.filedrive.repository;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.roadmap.filedrive.utils.MinioProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Repository
@RequiredArgsConstructor
public class MinioFileRepository {

    private final MinioClientBuilder builder;
    private final MinioProperties properties;
    private MinioClient client;
    private String bucket;

    @PostConstruct
    private void initialize() {
        client = builder.buildMinioClient();
        bucket = properties.getDefaultBucket();
    }

    public void put(String nameWithPath, long size, InputStream stream) throws MinioUnknownException {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(nameWithPath)
                    .stream(stream, size, -1)
                    .build());
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException e) {
            throw new MinioUnknownException(e);
        }
    }

    public GetObjectResponse get(String nameWithPath) throws MinioUnknownException {
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(nameWithPath)
                    .build());
        } catch (MinioException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
            throw new MinioUnknownException(e);
        }
    }

    public void delete(String nameWithPath) throws MinioUnknownException {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(nameWithPath)
                    .build());
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException e) {
            throw new MinioUnknownException(e);
        }
    }

    private void copy(String sourceNameWithPath, String targetNameWithPath) throws MinioUnknownException {
        try {
            client.copyObject(CopyObjectArgs.builder()
                    .bucket(bucket)
                    .source(CopySource.builder().bucket(bucket).object(sourceNameWithPath).build())
                    .object(targetNameWithPath).build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new MinioUnknownException(e);
        }
    }

    public void update(String oldName, String newName) throws MinioUnknownException {
        copy(oldName, newName);
        delete(oldName);
    }
}
