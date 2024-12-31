package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.ApplicationUserDTO;
import com.innovatrix.ahaar.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<ApplicationUser> getUsers() {
        return userRepository.findAll();

    }

    public void addUser(ApplicationUserDTO user) {
        Optional<ApplicationUser> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            throw new IllegalStateException("User with this email already exists");
        }
        userRepository.save(user.toEntity());
    }

    @Transactional
    public void updateUser(Long userId, ApplicationUserDTO user) {
        Optional<ApplicationUser> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("User with this id does not exist");
        }
        if(!user.getEmail().isEmpty() && !user.getUserName().isEmpty() && !user.getPassword().isEmpty()) {
            userRepository.save(user.toEntity());
        }
        throw new IllegalStateException(
                "Required fields are missing in update operation"
        );
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
