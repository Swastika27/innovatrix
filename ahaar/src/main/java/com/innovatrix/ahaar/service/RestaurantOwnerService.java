package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.DTO.JwtResponseDTO;
import com.innovatrix.ahaar.DTO.LoginDTO;
import com.innovatrix.ahaar.exception.UserNotFoundException;
import com.innovatrix.ahaar.model.RefreshToken;
import com.innovatrix.ahaar.model.RestaurantOwner;
import com.innovatrix.ahaar.repository.RestaurantOwnerRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantOwnerService {
    private final RestaurantOwnerRepository restaurantOwnerRepository;

    private AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JWTService jwtService;

    private final RedisService redisService;

    private final RefreshTokenService refreshTokenService;

    @Autowired
    public RestaurantOwnerService(RestaurantOwnerRepository restaurantOwnerRepository, AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder, JWTService jwtService, RedisService redisService, RefreshTokenService refreshTokenService) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
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

    public RestaurantOwner add(RestaurantOwner restaurantOwner) {
        Optional<RestaurantOwner> restaurantOwner1 = restaurantOwnerRepository.findByEmail(restaurantOwner.getEmail());
        if(restaurantOwner1.isPresent()) {
            throw new IllegalStateException("Restaurant owner with this email already exists");
        }

        restaurantOwner.setPassword(bCryptPasswordEncoder.encode(restaurantOwner.getPassword()));
        return restaurantOwnerRepository.save(restaurantOwner);
    }

    @Transactional
    public RestaurantOwner update(RestaurantOwner restaurantOwner) {
        Optional<RestaurantOwner> restaurantOwner1 = restaurantOwnerRepository.findById(restaurantOwner.getId());
        if(restaurantOwner1.isEmpty()) {
            throw new UserNotFoundException(restaurantOwner.getId());
        }

        restaurantOwner.setName(restaurantOwner1.get().getName());
        restaurantOwner.setEmail(restaurantOwner1.get().getEmail());
        restaurantOwner.setPhoneNumber(restaurantOwner1.get().getPhoneNumber());
        restaurantOwner.setPassword(bCryptPasswordEncoder.encode(restaurantOwner.getPassword()));
        return restaurantOwnerRepository.save(restaurantOwner);
    }

    public void delete(Long id) {
        Optional<RestaurantOwner> restaurantOwner1 = restaurantOwnerRepository.findById(id);
        if(restaurantOwner1.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        restaurantOwnerRepository.delete(restaurantOwner1.get());
    }

    public String login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));

        if(authentication.isAuthenticated()) {
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
                    return  jwtResponseDTO;
                }).orElse(null);

    }

}
