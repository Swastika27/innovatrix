package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.ApplicationUserDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserServiceInterface {
    Page<ApplicationUser> getUsers(int page, int size);

    Optional<ApplicationUser> addUser(ApplicationUserDTO user);

    ApplicationUser updateUser(Long userId, ApplicationUserDTO user);

    void deleteUser(Long id);

    Optional<ApplicationUser> getUserById(Long id);
}

