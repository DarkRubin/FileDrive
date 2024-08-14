package org.roadmap.filedrive.controller;

import org.roadmap.filedrive.dto.AppUserDTO;
import org.roadmap.filedrive.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthorizationController {

    @Autowired
    private AppUserService appUserService;

    @GetMapping("/sign-up")
    public String signUp(Model model) {
        AppUserDTO userDTO = new AppUserDTO();
        model.addAttribute(userDTO);
        return "registration";
    }

    @GetMapping("/sign-in")
    public String signIn() {
        return "authorization";
    }

    @GetMapping("/log-out")
    public String logOut() {
        return "logout";
    }

    @PostMapping("/sign-up")
    public void signUp() {
        //TODO
    }

    @PostMapping("/sign-in")
    public void signIn(String email, String password) {
        //TODO
    }

    @PostMapping("/log-out")
    public void logOut(User user) {
        //TODO
    }
}
