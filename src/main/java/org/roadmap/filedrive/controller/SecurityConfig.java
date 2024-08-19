package org.roadmap.filedrive.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/sign-in/**"),
                                new AntPathRequestMatcher("/sign-up"),
                                new AntPathRequestMatcher("/log-out"),
                                new AntPathRequestMatcher("/static/**"),
                                new AntPathRequestMatcher("/css/**")
                        ).permitAll()
                        .requestMatchers("/search")
                        .hasRole("USER")
                        .anyRequest().authenticated()
                ).formLogin(form -> form
                        .loginPage("/sign-in")
                        .loginProcessingUrl("/sign-in/auth")
                        .usernameParameter("email")
        ).logout(logout -> logout
                        .logoutUrl("/log-out")
                        .logoutSuccessUrl("/")
                ).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
