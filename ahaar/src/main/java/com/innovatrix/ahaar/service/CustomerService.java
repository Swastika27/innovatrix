package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.dto.CustomerDTO;
import com.innovatrix.ahaar.dto.JwtResponseDTO;
import com.innovatrix.ahaar.dto.LoginDTO;
import com.innovatrix.ahaar.exception.UserNotFoundException;
import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.Customer;
import com.innovatrix.ahaar.model.RefreshToken;
import com.innovatrix.ahaar.model.Role;
import com.innovatrix.ahaar.repository.CustomerRepository;
import com.innovatrix.ahaar.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CustomerService {
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private CustomerRepository customerRepository;
    private AuthenticationManager authenticationManager;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JWTService jwtService;
    private RedisService redisService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder, JWTService jwtService, RedisService redisService, RefreshTokenService refreshTokenService, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    public Page<Customer> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable);
    }

    public Optional<Customer> add(CustomerDTO customerDTO) {
        Optional<ApplicationUser> userOptional = userRepository.findByEmail(customerDTO.getEmail());
        if (userOptional.isPresent()) {
            throw new IllegalStateException("Customer with this email already exists");
        }
//        checkConditions(user);
        Customer customer = Customer.builder().user(new ApplicationUser(customerDTO.getUserName(), customerDTO.getEmail(), bCryptPasswordEncoder.encode(customerDTO.getPassword()), Role.CUSTOMER))
                .dateOfBirth(customerDTO.getDateOfBirth())
                .currentWorkPlace(customerDTO.getCurrentWorkPlace())
                .currentAddress(customerDTO.getCurrentAddress())
                .name(customerDTO.getName())
                .gender(customerDTO.getGender())
                .homeTown(customerDTO.getHomeTown())
                .educationalInstitution(customerDTO.getEducationalInstitution())
                .phoneNumber(customerDTO.getPhoneNumber()).build();
        return Optional.of(customerRepository.save(customer));
    }

    @Transactional
    public Customer updateInfo(Long userId, Customer user) {
        Optional<Customer> customerOptional = customerRepository.findById(userId);
        if (customerOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        customerOptional.get().getUser().setUserName(user.getUser().getUserName());
        customerOptional.get().getUser().setEmail(user.getUser().getEmail());
        customerOptional.get().getUser().setPassword(bCryptPasswordEncoder.encode(user.getUser().getPassword()));
        customerOptional.get().setCurrentAddress(user.getCurrentAddress());
        customerOptional.get().setGender(user.getGender());
        customerOptional.get().setName(user.getName());
        customerOptional.get().setPhoneNumber(user.getPhoneNumber());
        customerOptional.get().setCurrentWorkPlace(user.getCurrentWorkPlace());
        customerOptional.get().setDateOfBirth(user.getDateOfBirth());
        customerOptional.get().setEducationalInstitution(user.getEducationalInstitution());
        customerOptional.get().setHomeTown(user.getHomeTown());
        customerOptional.get().setPhoneNumber(user.getPhoneNumber());

        return customerRepository.save(customerOptional.get());
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
            }
        } catch (Exception e) {
            // user not in cache
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

        return null;
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
