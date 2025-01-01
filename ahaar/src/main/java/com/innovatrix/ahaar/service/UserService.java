package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.ApplicationUserDTO;
import com.innovatrix.ahaar.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserServiceInterface {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<ApplicationUser> getUsers() {
        return userRepository.findAll();

    }

    private void checkConditions(ApplicationUserDTO user) {
        if(user.getUserName().length() <= 0) {
            throw new IllegalStateException("User name is empty");
        }
        if(user.getUserName().length() > 100) {
            throw new IllegalStateException("User name is too long, must be less than 100 characters");
        }
        if(user.getPassword().length() <= 0) {
            throw new IllegalStateException("Password is empty");
        }
        if (user.getEmail().length() <= 0) {
            throw new IllegalStateException("Email is empty");
        }
        if(user.getEmail().length() > 100) {
            throw new IllegalStateException("Email is too long, must be less than 100 characters");
        }
    }

    public Optional<ApplicationUser> addUser(ApplicationUserDTO user) {
        Optional<ApplicationUser> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            throw new IllegalStateException("User with this email already exists");
        }
        checkConditions(user);
        userRepository.save(user.toEntity());
        return userRepository.findByEmail(user.getEmail());
    }

    @Transactional
    public ApplicationUser updateUser(Long userId, ApplicationUserDTO user) {
        Optional<ApplicationUser> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("User with this id does not exist");
        }
        if(user.getEmail().isEmpty() || user.getPassword().isEmpty() || user.getUserName().isEmpty()) {
            throw new IllegalStateException(
                    "Required fields are missing in update operation"
            );
        }

        checkConditions(user);
        userOptional.get().setUserName(user.getUserName());
        userOptional.get().setEmail(user.getEmail());
        userOptional.get().setPassword(user.getPassword());

        userRepository.save(userOptional.get());
        return userOptional.get();
    }

    public void deleteUser(Long id) {
        Optional<ApplicationUser> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("User with this id does not exist");
        }
        userRepository.deleteById(id);
    }

    public Optional<ApplicationUser> getUserById(Long id) {
        Optional<ApplicationUser> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("User with this id does not exist");
        }
        return userOptional;
    }
}
