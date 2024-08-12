package org.roadmap.filedrive.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @Value("${spring.application.name}")
    String appName;

    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("appName", appName);
        return "main";
    }

}
