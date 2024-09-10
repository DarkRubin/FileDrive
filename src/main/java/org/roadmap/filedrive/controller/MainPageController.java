package org.roadmap.filedrive.controller;

import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.dto.Breadcrumb;
import org.roadmap.filedrive.service.FileService;
import org.roadmap.filedrive.utils.PathAccessHandler;
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

    private final PathAccessHandler handler = new PathAccessHandler();

    @GetMapping({"/", "/main"})
    public String mainPage(Model model, @RequestParam(defaultValue = "") String path) {
        path = handler.validatePath(path);
        if (!isAuthenticated()) {
            return "main-not-auth";
        }
        model.addAttribute("files", service.getAllFileNames(path));
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
        model.addAttribute("files", service.getAllFileNames(path));
        model.addAttribute("path", path);
        addBreadcrumbs(model, path);
        model.addAttribute("searched", fileName);
        return "main";
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
