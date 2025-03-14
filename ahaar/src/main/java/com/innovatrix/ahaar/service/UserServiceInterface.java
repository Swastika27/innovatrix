package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.dto.ApplicationUserDTO;
import com.innovatrix.ahaar.dto.LoginDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserServiceInterface {
    Page<ApplicationUser> getAll(int page, int size);

    Optional<ApplicationUser> addUser(ApplicationUserDTO user);

    ApplicationUser updateUser(Long userId, ApplicationUserDTO user);

    void deleteUser(Long id);

    Optional<ApplicationUser> getUserById(Long id);

    String login(LoginDTO loginDTO);
}

