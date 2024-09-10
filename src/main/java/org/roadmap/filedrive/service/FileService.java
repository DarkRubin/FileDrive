package org.roadmap.filedrive.service;

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
        folderRepository.getAllContentNames(folder).forEach(itemResult -> {
            try {
                if (!itemResult.get().objectName().equals(folder)) {
                    result.add(itemResult.get().objectName());
                }
            } catch (Exception e) {
                throw new MinioUnknownException(e);
            }
        });
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

    public ByteArrayOutputStream getFolder(String folder, String path) throws IOException {
        String fullName = path + folder;
        var baos = new ByteArrayOutputStream();
        var zos = new ZipOutputStream(baos);
        List<String> allFiles = new ArrayList<>();
        Queue<String> queue = new LinkedList<>(getAllFileNames(fullName));
        while (!queue.isEmpty()) {
            String fileName = queue.poll();
            if (fileName.endsWith("/")) {
                queue.addAll(getAllFileNames(fileName));
            }
            allFiles.add(fileName);
        }
        for (String fileNameWithPath : allFiles) {
            String fileName = fileNameWithPath.replace(path, "");
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            zos.write(get(fileNameWithPath).getContentAsByteArray());
            zos.closeEntry();
        }
        zos.close();
        return baos;
    }
}
