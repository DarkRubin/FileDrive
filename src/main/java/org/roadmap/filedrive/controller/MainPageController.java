package org.roadmap.filedrive.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @Value("${spring.application.name}")
    String appName;

    @GetMapping({"/", "/main"})
    public String mainPage(Model model) {
        model.addAttribute("appName", appName);
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
