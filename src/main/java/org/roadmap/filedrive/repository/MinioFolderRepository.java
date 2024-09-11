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
import org.roadmap.filedrive.utils.MinioProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Repository
@RequiredArgsConstructor
public class MinioFolderRepository {

    private final MinioClientBuilder builder;
    private final MinioProperties properties;
    private MinioClient client;
    private String bucket;

    @PostConstruct
    private void initialize() {
        client = builder.buildMinioClient();
        bucket = properties.getDefaultBucket();
    }

    public List<String> listEntriesNameWithNested(String path) {
        List<String> result = new ArrayList<>();
        Queue<String> queue = new LinkedList<>(listEntriesName(path));
        while (!queue.isEmpty()) {
            String fileName = queue.poll();
            if (fileName.endsWith("/")) {
                queue.addAll(listEntriesName(fileName));
            } else result.add(fileName);
        }
        return result;
    }

    public List<String> listEntriesName(String path) throws MinioUnknownException {
        List<String> result = new ArrayList<>();
        Iterable<Result<Item>> listedObjects = client.listObjects(ListObjectsArgs.builder()
                .bucket(bucket).prefix(path).build());
        try {
            for (Result<Item> object : listedObjects) {
                String fileName = object.get().objectName();
                if (fileName.equals(path)) continue;
                result.add(fileName);
            }
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new MinioUnknownException(e);
        }
        return result;
    }

    public void delete(String path) throws MinioUnknownException {
        List<DeleteObject> objectsToDelete = new ArrayList<>();
        for (String fileName : listEntriesNameWithNested(path)) {
            objectsToDelete.add(new DeleteObject(fileName));
        }
        client.removeObjects(RemoveObjectsArgs.builder()
                .bucket(bucket)
                .objects(objectsToDelete)
                .build());
    }
}
