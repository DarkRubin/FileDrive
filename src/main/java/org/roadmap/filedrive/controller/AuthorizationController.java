package org.roadmap.filedrive.controller;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthorizationController {

    @GetMapping("/sign-up")
    public String signUp() {
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
    public void signUp(String email, String password) {
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
