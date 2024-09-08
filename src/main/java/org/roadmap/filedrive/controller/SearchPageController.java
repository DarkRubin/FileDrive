package org.roadmap.filedrive.controller;

import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.roadmap.filedrive.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchPageController {

    private final FileService service;

    private final PathAccessHandler handler = new PathAccessHandler();

    @GetMapping("/search")
    public String searchPage(@RequestParam String input, @RequestParam String path, Model model) {
        path = handler.validatePath(path);
        model.addAttribute("searchInput", input);
        try {
            model.addAttribute("result", service.search(input, path));
        } catch (MinioUnknownException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "search";
    }
}
