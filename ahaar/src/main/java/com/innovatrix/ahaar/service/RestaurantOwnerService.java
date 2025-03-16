package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.dto.JwtResponseDTO;
import com.innovatrix.ahaar.dto.LoginDTO;
import com.innovatrix.ahaar.dto.RestaurantOwnerDTO;
import com.innovatrix.ahaar.exception.UserNotFoundException;
import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.RefreshToken;
import com.innovatrix.ahaar.model.RestaurantOwner;
import com.innovatrix.ahaar.model.Role;
import com.innovatrix.ahaar.repository.RestaurantOwnerRepository;
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
public class RestaurantOwnerService {
    private final RestaurantOwnerRepository restaurantOwnerRepository;

    private AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JWTService jwtService;

    private final UserRepository userRepository;

    private final RedisService redisService;

    private final RefreshTokenService refreshTokenService;

    @Autowired
    public RestaurantOwnerService(RestaurantOwnerRepository restaurantOwnerRepository, AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder, JWTService jwtService, UserRepository userRepository, RedisService redisService, RefreshTokenService refreshTokenService) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.refreshTokenService = refreshTokenService;
    }

    public Page<RestaurantOwner> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantOwnerRepository.findAll(pageable);
    }

    public RestaurantOwner getById(Long id) {
        return restaurantOwnerRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );
    }

    public RestaurantOwner add(RestaurantOwnerDTO restaurantOwnerDTO) {
        Optional<ApplicationUser> user = userRepository.findByEmail(restaurantOwnerDTO.getEmail());
        if (user.isPresent()) {
            throw new IllegalStateException("This email is already in use");
        }

        RestaurantOwner restaurantOwner = new RestaurantOwner(new ApplicationUser(restaurantOwnerDTO.getUserName(), restaurantOwnerDTO.getEmail(), bCryptPasswordEncoder.encode(restaurantOwnerDTO.getPassword()), Role.RESTAURANT_OWNER),
                restaurantOwnerDTO.getName(), restaurantOwnerDTO.getPhoneNumber(), restaurantOwnerDTO.getNID());
        return restaurantOwnerRepository.save(restaurantOwner);
    }

    @Transactional
    public RestaurantOwner update(RestaurantOwner restaurantOwner) {
        Optional<RestaurantOwner> restaurantOwner1 = restaurantOwnerRepository.findById(restaurantOwner.getId());
        if (restaurantOwner1.isEmpty()) {
            throw new UserNotFoundException(restaurantOwner.getId());
        }

        restaurantOwner.setName(restaurantOwner1.get().getName());
        restaurantOwner.getUser().setEmail(restaurantOwner1.get().getUser().getEmail());
        restaurantOwner.setPhoneNumber(restaurantOwner1.get().getPhoneNumber());
        restaurantOwner.getUser().setPassword(bCryptPasswordEncoder.encode(restaurantOwner.getUser().getPassword()));
        return restaurantOwnerRepository.save(restaurantOwner);
    }

    public void delete(Long id) {
        Optional<RestaurantOwner> restaurantOwner1 = restaurantOwnerRepository.findById(id);
        if (restaurantOwner1.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        restaurantOwnerRepository.delete(restaurantOwner1.get());
    }

    public String login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));

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
                }).orElse(null);

    }

}
