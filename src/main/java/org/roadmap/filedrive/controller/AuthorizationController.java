package org.roadmap.filedrive.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.roadmap.filedrive.dto.UserDTO;
import org.roadmap.filedrive.maper.UserMapper;
import org.roadmap.filedrive.maper.UserMapperImpl;
import org.roadmap.filedrive.model.User;
import org.roadmap.filedrive.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthorizationController {

    private final UserRepository repo;

    private final UserMapper mapper = new UserMapperImpl();

    public AuthorizationController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/sign-up")
    public String signUp(Model model) {
        model.addAttribute("userForm", new UserDTO());
        return "sign-up";
    }


    @GetMapping("/sign-in")
    public String signIn(Model model) {
        model.addAttribute("userForm", new UserDTO());
        return "sign-in";
    }

    @GetMapping("/logout")
    public String logOut() {
        return "logout";
    }

    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute("userForm") @Valid UserDTO userForm,
                         BindingResult result, HttpServletRequest request) {
        User user = repo.findByEmail(userForm.getEmail());
        if (user != null) {
            result.addError(new FieldError("userForm", "email",
                    "Email is already used"));
        }
        String password = userForm.getPassword();
        validate(password, result);

        if (result.hasErrors()) {
            return "sign-up";
        }

        try {
            var bCryptEncoder = new BCryptPasswordEncoder();
            String encodedPassword = bCryptEncoder.encode(password);
            userForm.setPassword(encodedPassword);
            User userToSave = mapper.fromDTO(userForm);
            repo.save(userToSave);
            request.login(userForm.getEmail(), password);
        } catch (Exception e) {
            result.addError(new FieldError("userForm", "email",
                    "Unknown error occurred"));
            return "sign-up";
        }

        return "redirect:/main";
    }

    private void validate(String password, BindingResult result) {
        if (password.length() < 8) {
            result.addError(new FieldError("userForm", "password",
                    "Minimum password length 8 characters"));
            return;
        }

        int lettersCount = 0;
        int numbersCount = 0;
        int specialSymbolsCount = 0;
        for (char c : password.toCharArray()) {
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
    }
}
