package org.roadmap.filedrive.service;

import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.repository.MinioFileRepository;
import org.roadmap.filedrive.repository.MinioFolderRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioFileRepository fileRepository;

    private final MinioFolderRepository folderRepository;

    public List<String> getAllFileNames(String folder) {
        return folderRepository.listEntriesName(folder).reversed();
    }

    public InputStreamResource get(String fullName) {
        return new InputStreamResource(fileRepository.get(fullName));
    }

    public void put(String fullName, long length, InputStream file) {
        fileRepository.put(fullName, length, file);
    }

    public void delete(String fullName) {
        fileRepository.delete(fullName);
    }

    public void rename(String fullOldName, String fullNewName) {
        fileRepository.update(fullOldName, fullNewName);
    }

    public List<String> search(String toSearch, String diapason) {
        List<String> result = new ArrayList<>();

        String fullName = diapason + toSearch;
        folderRepository.listEntriesNameWithNested(fullName).forEach(fileNameWithPath -> {
            String fileName = fileNameWithPath.replace(diapason, "");
            if (fileName.toLowerCase().contains(toSearch.toLowerCase())) {
                result.add(fileName);
            }
        });

        return result;
    }

    public ByteArrayOutputStream getFolder(String folder, String path) throws IOException {
        String fullName = path + folder;
        var baos = new ByteArrayOutputStream();
        var zos = new ZipOutputStream(baos);
        for (String fileNameWithPath : folderRepository.listEntriesNameWithNested(fullName)) {
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
