package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.ApplicationUserDetails;
import com.innovatrix.ahaar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public ApplicationUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ApplicationUser> user = userRepository.findByUserName(username);
        if (user.isEmpty()) {
            System.out.println(username + " not found");
            throw new UsernameNotFoundException(username + " not found");
        }
        return new ApplicationUserDetails(user.get());
    }
}
