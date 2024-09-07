package org.roadmap.filedrive.service;

import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.roadmap.filedrive.repository.MinioFileRepository;
import org.roadmap.filedrive.repository.MinioFolderRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioFileRepository fileRepository;

    private final MinioFolderRepository folderRepository;

    public List<String> getAllFileNames(String folder) throws MinioUnknownException {
        ArrayList<String> result = new ArrayList<>();
        Iterator<Result<Item>> iterator = folderRepository.getAllContentNames(folder).iterator();
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

    public InputStreamResource get(String fullName) throws MinioUnknownException, IOException {
        var byteStream = new ByteArrayInputStream(fileRepository.get(fullName).readAllBytes());
        return new InputStreamResource(byteStream);
    }

    public void put(String fullName, long length, InputStream file) throws MinioUnknownException, IOException {
        fileRepository.put(fullName, length, file);
    }

    public void delete(String fullName) throws IOException, MinioUnknownException {
        fileRepository.delete(fullName);
    }

    public void rename(String fullOldName, String fullNewName) throws IOException, MinioUnknownException {
        fileRepository.update(fullOldName, fullNewName);
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
