package org.roadmap.filedrive.controller;

import org.junit.jupiter.api.Test;
import org.roadmap.filedrive.TestcontainersConfiguration;
import org.roadmap.filedrive.model.User;
import org.roadmap.filedrive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class AuthorizationControllerIT {

    public static final String EMAIL = "correctEmail@superMail.com";
    public static final String PASSWORD = "*correctPassword123*";

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    void signUp_RequestValid_ReturnsRedirectToMainPage() throws Exception {
        userRepository.deleteAll();
        var requestBuilder = MockMvcRequestBuilders.post("/sign-up")
                .param("email", EMAIL)
                .param("password", PASSWORD)
                .with(csrf());

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main"));
        User result = userRepository.findByEmail(EMAIL);
        assertNotNull(result);

    }

    @Test
    void signUp_RequestInvalid_ReturnsToSignUpPage() throws Exception {
        userRepository.deleteAll();
        var requestBuilder = MockMvcRequestBuilders.post("/sign-up")
                .param("email", "incorrect")
                .param("password", "incorrect")
                .with(csrf());

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"))
                .andExpect(model().attributeHasFieldErrors("userForm", "password"));
    }

    @Test
    void signUp_RequestAlreadyUsed_ReturnsRedirectToSignUpPage() throws Exception {
        userRepository.deleteAll();
        userRepository.save(new User(EMAIL, "anyPassword"));
        var requestBuilder = MockMvcRequestBuilders.post("/sign-up")
                .param("email", EMAIL)
                .param("password", PASSWORD)
                .with(csrf());
        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userForm", "email"));
    }
}