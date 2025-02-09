package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.DTO.JwtResponseDTO;
import com.innovatrix.ahaar.DTO.LoginDTO;
import com.innovatrix.ahaar.exception.UserNotFoundException;
import com.innovatrix.ahaar.model.Customer;
import com.innovatrix.ahaar.model.RefreshToken;
import com.innovatrix.ahaar.repository.CustomerRepository;
import com.innovatrix.ahaar.util.ResponseBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private final RefreshTokenService refreshTokenService;
    private CustomerRepository customerRepository;
    private AuthenticationManager authenticationManager;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JWTService jwtService;
    private RedisService redisService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder, JWTService jwtService, RedisService redisService, RefreshTokenService refreshTokenService) {
        this.customerRepository = customerRepository;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.refreshTokenService = refreshTokenService;
    }

    public Page<Customer> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable);
    }

    public Optional<Customer> add(Customer customer) {
        Optional<Customer> customerOptional = customerRepository.findByEmail(customer.getEmail());
        if (customerOptional.isPresent()) {
            throw new IllegalStateException("Customer with this email already exists");
        }
//        checkConditions(user);
        customer.getUser().setPassword(bCryptPasswordEncoder.encode(customer.getUser().getPassword()));
        return Optional.of(customerRepository.save(customer));
    }

    @Transactional
    public Customer updateInfo(Long userId, Customer user) {
        Optional<Customer> userOptional = customerRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        if (user.getEmail().isEmpty() || user.getUser().getPassword().isEmpty() || user.getUser().getUserName().isEmpty()) {
            throw new IllegalArgumentException(
                    "Required fields are missing in update operation"
            );
        }

        userOptional.get().getUser().setUserName(user.getUser().getUserName());
        userOptional.get().setEmail(user.getEmail());
        userOptional.get().getUser().setPassword(bCryptPasswordEncoder.encode(user.getUser().getPassword()));
        userOptional.get().setCurrentAddress(user.getCurrentAddress());
        userOptional.get().setGender(user.getGender());
        userOptional.get().setName(user.getName());
        userOptional.get().setPhoneNumber(user.getPhoneNumber());
        userOptional.get().setCurrentWorkPlace(user.getCurrentWorkPlace());
        userOptional.get().setDateOfBirth(user.getDateOfBirth());
        userOptional.get().setEducationalInstitution(user.getEducationalInstitution());
        userOptional.get().setHomeTown(user.getHomeTown());
        userOptional.get().setEmail(user.getEmail());
        userOptional.get().setPhoneNumber(user.getPhoneNumber());

        return customerRepository.save(userOptional.get());
    }

    public void deleteUser(Long id) {
        Optional<Customer> userOptional = customerRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }

    public Optional<Customer> getUserById(Long id) {

        try {
            String redisKey = RedisService.REDIS_PREFIX + id;
            // Check if the user is in Redis cache
            Customer cachedUser = redisService.get(redisKey, Customer.class);

            if (cachedUser != null) {
                // Return user from Redis cache
                return Optional.of(cachedUser);
            } else {
                throw new UserNotFoundException(id);
            }
        } catch (Exception e) {
            Optional<Customer> userOptional = customerRepository.findById(id);
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

    public String login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(loginDTO.getUsername());
        }
        throw new IllegalStateException("Authentication failed");
    }

    public JwtResponseDTO refresh(String token) {
        return refreshTokenService.findByToken(token)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getUserName());
                    JwtResponseDTO jwtResponseDTO = JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(token)
                            .build();
                    return jwtResponseDTO;
                }).orElseGet(() -> null);

    }
}
