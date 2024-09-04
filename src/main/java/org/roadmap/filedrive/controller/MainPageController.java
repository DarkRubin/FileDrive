package org.roadmap.filedrive.controller;

import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.roadmap.filedrive.service.FileService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MainPageController {

    private final FileService service;

    @GetMapping({"/", "/main"})
    public String mainPage(Model model) {
        model.addAttribute("appName", appName);
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
        return "main";
    }

        return "main";
    }

    @GetMapping("/search")
    public String searchPage(Model model) {
        model.addAttribute("appName", appName);
        return "search";
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


}
