package org.roadmap.filedrive.controller;

import jakarta.validation.Valid;
import org.roadmap.filedrive.dto.AppUserDTO;
import org.roadmap.filedrive.maper.AppUserMapper;
import org.roadmap.filedrive.maper.AppUserMapperImpl;
import org.roadmap.filedrive.model.AppUser;
import org.roadmap.filedrive.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthorizationController {

    @Autowired
    private AppUserRepository repo;

    private final AppUserMapper mapper = new AppUserMapperImpl();

    @GetMapping("/sign-up")
    public String signUp(Model model) {
        model.addAttribute("userForm", new AppUserDTO());
        return "sign-up";
    }

    @GetMapping("/sign-in")
    public String signIn(Model model) {
        model.addAttribute("userForm", new AppUserDTO());
        return "sign-in";
    }

    @GetMapping("/log-out")
    public String logOut() {
        return "logout";
    }

    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute("userForm") @Valid AppUserDTO userForm, BindingResult result) {
        AppUser appUser = repo.findByEmail(userForm.getEmail());
        if (appUser != null) {
            result.addError(new FieldError("userForm", "email",
                    "Email is already used"));
        }
        int lettersCount = 0;
        int numbersCount = 0;
        int specialSymbolsCount = 0;
        for (char c : userForm.getPassword().toCharArray()) {
            if (Character.isLetter(c)) {
                lettersCount++;
            } else if (Character.isDigit(c)) {
                numbersCount++;
            } else {
                specialSymbolsCount++;
            }
        }
        if (lettersCount == 0) {
            result.addError(new FieldError("userForm", "password",
                    "Password must contain at least one letter"));
        }
        if (numbersCount == 0) {
            result.addError(new FieldError("userForm", "password",
                    "Password must contain at least one number"));
        }
        if (specialSymbolsCount == 0) {
            result.addError(new FieldError("userForm", "password",
                    "Password must contain at least one special symbol"));
        }

        if (result.hasErrors()) {
            return "sign-up";
        }

        try {
            var bCryptEncoder = new BCryptPasswordEncoder();
            String encodedPassword = bCryptEncoder.encode(userForm.getPassword());
            userForm.setPassword(encodedPassword);
            AppUser userToSave = mapper.fromDTO(userForm);
            repo.save(userToSave);
        } catch (Exception e) {
            result.addError(new FieldError("userForm", "email",
                    "Unknown error occurred"));
            return "sign-up";
        }
        return "redirect:/main";
    }

    @PostMapping("/sign-in")
    public String signIn(@ModelAttribute("userForm") AppUserDTO userForm, BindingResult result) {


        AppUser appUser = repo.findByEmail(userForm.getEmail());
        if (appUser == null) {
            result.addError(new FieldError("userForm", "email",
                    "Incorrect email or password"));
        }
        if (result.hasErrors()) {
            return "sign-in";
        }

        return "redirect:/main";
    }

    @PostMapping("/log-out")
    public void logOut(User user) {
        //TODO
    }
}
