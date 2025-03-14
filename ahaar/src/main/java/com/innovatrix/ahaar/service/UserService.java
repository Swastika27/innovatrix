package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.exception.UserNotFoundException;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.dto.ApplicationUserDTO;
import com.innovatrix.ahaar.dto.LoginDTO;
import com.innovatrix.ahaar.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserServiceInterface {

    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private JWTService jwtService;

    private RedisService redisService;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder, JWTService jwtService, RedisService redisService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.redisService = redisService;
    }

    public Page<ApplicationUser> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
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
//        checkConditions(user);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return Optional.of(userRepository.save(user.toEntity()));
    }

    @Transactional
    public ApplicationUser updateUser(Long userId, ApplicationUserDTO user) {
        Optional<ApplicationUser> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        if(user.getEmail().isEmpty() || user.getPassword().isEmpty() || user.getUserName().isEmpty()) {
            throw new IllegalArgumentException(
                    "Required fields are missing in update operation"
            );
        }

//        checkConditions(user);
        userOptional.get().setUserName(user.getUserName());
        userOptional.get().setEmail(user.getEmail());
        userOptional.get().setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return userRepository.save(userOptional.get());
    }

    public void deleteUser(Long id) {
        Optional<ApplicationUser> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public Optional<ApplicationUser> getUserById(Long id) {

        try {
            String redisKey = RedisService.REDIS_PREFIX + id;
            // Check if the user is in Redis cache
            ApplicationUser cachedUser = redisService.get(redisKey, ApplicationUser.class);

            if (cachedUser != null) {
                // Return user from Redis cache
                return Optional.of(cachedUser);
            } else {
                throw new UserNotFoundException(id);
            }
        } catch (Exception e) {
            Optional<ApplicationUser> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {

                throw new UserNotFoundException(id);
            }
            try {
                redisService.set(RedisService.REDIS_PREFIX + id, userOptional.get(), 1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return userOptional;
        }
    }
    @Override
    public String login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(loginDTO.getUsername());
        }
        throw new IllegalStateException("Authentication failed");
    }

}
