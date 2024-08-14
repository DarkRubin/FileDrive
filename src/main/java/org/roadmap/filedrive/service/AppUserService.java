package org.roadmap.filedrive.service;

import org.roadmap.filedrive.model.AppUser;
import org.roadmap.filedrive.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        } else {
            return User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .build();
        }
    }
}
