package org.roadmap.filedrive.repository;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Item;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Repository
public class MinioRepository {

    private static final String BUCKET = "main-repository";

    private static final MinioClient MINIO_CLIENT = MinioClient.builder()
            .endpoint("http://172.19.0.4:9000")
            .credentials("root", "mg3hg3i8qc")
            .build();

    public MinioRepository() throws MinioUnknownException {
        try {
            if (!MINIO_CLIENT.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build())) {
                MINIO_CLIENT.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
            }
        } catch (Exception e) {
            throw new MinioUnknownException(e);
        }
    }

    public void putFile(String nameWithPath, long size, InputStream stream) throws IOException, MinioUnknownException {
        try {
            MINIO_CLIENT.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET)
                    .object(nameWithPath)
                    .stream(stream, size, -1)
                    .build());
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new MinioUnknownException(e);
        }
    }

    public Iterable<Result<Item>> getAllFiles(String path) {
        return MINIO_CLIENT.listObjects(ListObjectsArgs.builder()
                .bucket(BUCKET).prefix(path).build());
    }

    public GetObjectResponse getFile(String nameWithPath) throws MinioUnknownException {
        try {
            return MINIO_CLIENT.getObject(GetObjectArgs.builder()
                    .bucket(BUCKET)
                    .object(nameWithPath)
                    .build());
        } catch (InvalidKeyException | NoSuchAlgorithmException | ErrorResponseException | InsufficientDataException |
                 InternalException | InvalidResponseException | IOException | ServerException | XmlParserException e) {
            throw new MinioUnknownException(e);
        }
    }

    public void deleteFile(String nameWithPath) throws IOException, MinioUnknownException {
        try {
            MINIO_CLIENT.removeObject(RemoveObjectArgs.builder()
                    .bucket(BUCKET)
                    .object(nameWithPath)
                    .build());
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new MinioUnknownException(e);
        }
    }

    public void updateFile(String oldName, String newName) throws MinioUnknownException, IOException {
        GetObjectResponse file = getFile(oldName);
        byte[] bytes = file.readAllBytes();
        putFile(newName, bytes.length, new ByteArrayInputStream(bytes));
        deleteFile(oldName);
    }
}
