package org.roadmap.filedrive.service;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.roadmap.filedrive.repository.MinioRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioRepository repository;

    public List<String> getAllFileNames(String folder) throws MinioUnknownException {
        ArrayList<String> result = new ArrayList<>();
        Iterator<Result<Item>> iterator = repository.getAllFiles(folder).iterator();
        if (iterator.hasNext()) {
            iterator.next();
        }
        while (iterator.hasNext()) {
            Result<Item> files = iterator.next();
            try {
                result.add(files.get().objectName());
            } catch (Exception e) {
                throw new MinioUnknownException(e);
            }
        }
        return result.reversed();
    }

    public byte[] get(String fullName) throws MinioUnknownException {
        try {
            return repository.getFile(fullName).readAllBytes();
        } catch (IOException e) {
            throw new MinioUnknownException(e);
        }
    }

    public void put(String fullName, long length, InputStream file) throws MinioUnknownException, IOException {
        repository.putFile(fullName, length, file);
    }

    public void delete(String fullName) throws IOException, MinioUnknownException {
        repository.deleteFile(fullName);
    }

    public void rename(String fullOldName, String fullNewName) throws IOException, MinioUnknownException {
        repository.updateFile(fullOldName, fullNewName);
    }

    public List<String> search(String toSearch, String diapason) throws MinioUnknownException {
        List<String> result = new ArrayList<>();

        Queue<String> queue = new LinkedList<>(getAllFileNames(diapason));

        while (!queue.isEmpty()) {
            String file = queue.remove();
            if (file.toLowerCase().contains(toSearch.toLowerCase())) {
                result.add(file);
            } else if (file.endsWith("/")) {
                queue.addAll(getAllFileNames(file));
            }
        }

        return result;
    }
}
