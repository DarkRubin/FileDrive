package org.roadmap.filedrive.repository;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MinioFolderRepository {

    private final MinioClientBuilder builder;
    private MinioClient client;
    private String bucket;

    @PostConstruct
    private void initialize() {
        client = builder.buildMinioClient();
        bucket = builder.getDefaultBucket();
    }

    public Iterable<Result<Item>> getAllContentNames(String path) throws MinioUnknownException {
        return client.listObjects(ListObjectsArgs.builder()
                .bucket(bucket).prefix(path).build());
    }

    public void delete(String path) throws MinioUnknownException {
        try {
            List<DeleteObject> objectsToDelete = new ArrayList<>();
            for (Result<Item> itemResult : getAllContentNames(path)) {
                objectsToDelete.add(new DeleteObject(itemResult.get().objectName()));
            }
            client.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(bucket)
                    .objects(objectsToDelete)
                    .build());
        } catch (IOException | MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new MinioUnknownException(e);
        }
    }
}
