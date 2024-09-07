package org.roadmap.filedrive.controller;

import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.roadmap.filedrive.service.FileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService service;


    @GetMapping("/download")
    public ResponseEntity<Resource> send(@RequestParam("filename") String fileName, @RequestParam(defaultValue = "") String path) {
        String fullName = path + fileName;
        InputStreamResource resourceStream;
        try {
            resourceStream = service.get(fullName);
        } catch (MinioUnknownException | IOException e) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(fileName, StandardCharsets.UTF_8).build();
        headers.setContentDisposition(contentDisposition);
        return new ResponseEntity<>(resourceStream, headers, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public String uploadFiles(@RequestParam("files") List<MultipartFile> files,
                              @RequestParam(defaultValue = "") String path, Model model) throws IOException {
        for (MultipartFile file : files) {
            String fullName = path + file.getOriginalFilename();
            try {
                service.put(fullName, file.getSize(), file.getInputStream());
            } catch (MinioUnknownException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        model.addAttribute("path", path);
        return "redirect:/";
    }

    @PostMapping("/rename")
    public String rename(@RequestParam String oldName, @RequestParam String newName,
                         @RequestParam(defaultValue = "") String path, Model model) throws IOException {
        String fullOldName = path + oldName;
        String fullNewName = path + newName;
        try {
            service.rename(fullOldName, fullNewName);
        } catch (MinioUnknownException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("path", path);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("fileName") String fileName, @RequestParam String path, Model model) throws IOException {
        String fullName = path + fileName;
        try {
            service.delete(fullName);
        } catch (MinioUnknownException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("path", path);
        return "redirect:/";
    }

    @PostMapping("/new-folder")
    public String newFolder(@RequestParam String name, @RequestParam String path, Model model)
            throws IOException, MinioUnknownException {
        String fullName = path + name + "/";
        service.put(fullName, 0, new ByteArrayInputStream(new byte[0]));
        model.addAttribute("path", path);
        return "redirect:/";
    }


}
