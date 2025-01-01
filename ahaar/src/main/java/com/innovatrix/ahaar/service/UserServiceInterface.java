package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.ApplicationUserDTO;

import java.util.List;
import java.util.Optional;

public interface UserServiceInterface {
    List<ApplicationUser> getUsers();

    Optional<ApplicationUser> addUser(ApplicationUserDTO user);

    ApplicationUser updateUser(Long userId, ApplicationUserDTO user);

    void deleteUser(Long id);

    Optional<ApplicationUser> getUserById(Long id);
}

