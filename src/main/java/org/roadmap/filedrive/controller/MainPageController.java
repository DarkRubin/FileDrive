package org.roadmap.filedrive.controller;

import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.dto.Breadcrumb;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.roadmap.filedrive.service.FileService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainPageController {

    private final FileService service;

    @GetMapping({"/", "/main"})
    public String mainPage(Model model, @RequestParam(defaultValue = "") String path) {
        if (!isAuthenticated()) {
            return "main-not-auth";
        }
        if (path == null || path.isEmpty()) {
            path = getPath();
        }

        try {
            model.addAttribute("files", service.getAllFileNames(path));
        } catch (MinioUnknownException e) {
            model.addAttribute("error", "Unknown");
        }
        model.addAttribute("path", path);
        addBreadcrumbs(model, path);
        return "main";
    }

    @PostMapping("/main")
    public String mainPage(@RequestParam String searchedFullPath, Model model) {
        String[] splitPath = searchedFullPath.split("/");
        int last = splitPath.length - 1;
        String fileName = splitPath [last];
        splitPath [last] = "";
        String path = String.join("/", splitPath);
        try {
            model.addAttribute("files", service.getAllFileNames(path));
        } catch (MinioUnknownException e) {
            model.addAttribute("error", "Unknown");
        }
        model.addAttribute("path", path);
        addBreadcrumbs(model, path);
        model.addAttribute("searched", fileName);
        return "main";
    }

    private String getPath() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .iterator().next().getAuthority();
    }

    private boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    private void addBreadcrumbs(Model model, String path) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        String[] split = path.split("/");
        for (String folder : split) {
            int i = path.lastIndexOf(folder);
            String breadcrumbPath = path.substring(0, i) + folder;
            breadcrumbs.add(new Breadcrumb(folder, breadcrumbPath));
        }
        model.addAttribute("breadcrumbs", breadcrumbs);
    }

}
