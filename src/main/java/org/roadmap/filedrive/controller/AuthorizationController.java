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
    public String signUp(AppUserDTO appUserDTO) {
        return "sign-up";
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
    public String signUp(@ModelAttribute @Valid AppUserDTO appUserDTO, BindingResult result) {
        AppUser appUser = repo.findByEmail(appUserDTO.getEmail());
        if (appUser != null) {
            result.addError(new FieldError("appUserDTO", "email",
                    "Email is already used"));
        }
        int lettersCount = 0;
        int numbersCount = 0;
        int specialSymbolsCount = 0;
        for (char c : appUserDTO.getPassword().toCharArray()) {
            if (Character.isLetter(c)) {
                lettersCount++;
            } else if (Character.isDigit(c)) {
                numbersCount++;
            } else {
                specialSymbolsCount++;
            }
        }
        if (lettersCount == 0) {
            result.addError(new FieldError("appUserDTO", "password",
                    "Password must contain at least one letter"));
        }
        if (numbersCount == 0) {
            result.addError(new FieldError("appUserDTO", "password",
                    "Password must contain at least one number"));
        }
        if (specialSymbolsCount == 0) {
            result.addError(new FieldError("appUserDTO", "password",
                    "Password must contain at least one special symbol"));
        }

        if (result.hasErrors()) {
            return "sign-up";
        }

        try {
            var bCryptEncoder = new BCryptPasswordEncoder();
            String encodedPassword = bCryptEncoder.encode(appUserDTO.getPassword());
            appUserDTO.setPassword(encodedPassword);
            AppUser userToSave = mapper.fromDTO(appUserDTO);
            repo.save(userToSave);
        } catch (Exception e) {
            result.addError(new FieldError("appUserDTO", "email",
                    "Unknown error occurred"));
        }
        return "redirect:/main";
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
