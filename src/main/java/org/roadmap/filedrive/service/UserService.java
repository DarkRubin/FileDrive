package org.roadmap.filedrive.service;

import org.roadmap.filedrive.model.User;
import org.roadmap.filedrive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        } else {
            return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .build();
        }
    }
}
