package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.dto.RestaurantRequestDTO;
import com.innovatrix.ahaar.dto.JwtResponseDTO;
import com.innovatrix.ahaar.dto.LoginDTO;
import com.innovatrix.ahaar.dto.RestaurantOwnerDTO;
import com.innovatrix.ahaar.exception.UserNotFoundException;
import com.innovatrix.ahaar.model.*;
import com.innovatrix.ahaar.repository.RestaurantOwnerRepository;
import com.innovatrix.ahaar.repository.RestaurantRepository;
import com.innovatrix.ahaar.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
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
@Slf4j
public class RestaurantOwnerService {

    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private final RestaurantRepository restaurantRepository;

    private AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JWTService jwtService;

    private final UserRepository userRepository;

    private final RedisService redisService;

    private final RefreshTokenService refreshTokenService;

    private final LocationService locationService;

    @Autowired
    public RestaurantOwnerService(RestaurantOwnerRepository restaurantOwnerRepository, AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder, JWTService jwtService, UserRepository userRepository, RedisService redisService, RefreshTokenService refreshTokenService, LocationService locationService, RestaurantRepository restaurantRepository) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.refreshTokenService = refreshTokenService;
        this.locationService = locationService;
        this.restaurantRepository = restaurantRepository;
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
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(token)
                            .build();
                }).orElse(null);

    }

    public Restaurant addRestaurant(String userName, RestaurantRequestDTO requestDTO) {
        log.info("RestaruantOwnerService: addRestaurant");
        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        log.info("User: {}", user);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not logged in");
        }
        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        log.info("Restaurant owner: {}", owner);
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        Point location = locationService.createPoint(requestDTO.getLocationDTO().getLatitude(), requestDTO.getLocationDTO().getLongitude());
        Restaurant restaurant = requestDTO.getRestaurantDTO().toRestaurant(owner.get(), location);
        owner.get().addRestaurant(restaurant);
        log.info("Restaurant added: {}", restaurant);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Restaurant updateRestaurant(String userName, Long restaurantId, RestaurantRequestDTO restaurantRequestDTO) {
        log.info("RestaruantOwnerService: addRestaurant");
        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        log.info("User: {}", user);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not logged in");
        }
        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        log.info("Restaurant owner: {}", owner);
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        log.info("Restaurant: {}", restaurant);
        if(restaurant.isEmpty()) {
            throw new IllegalStateException("Restaurant not found");
        }

        restaurant.get().setName(restaurantRequestDTO.getRestaurantDTO().getName());
        restaurant.get().setContactNumber(restaurantRequestDTO.getRestaurantDTO().getContactNumber());
        Point location = locationService.createPoint(restaurantRequestDTO.getLocationDTO().getLatitude(), restaurantRequestDTO.getLocationDTO().getLongitude());
        restaurant.get().setLocation(location);
        restaurant.get().setCuisine(restaurantRequestDTO.getRestaurantDTO().getCuisine());
        restaurant.get().setOpenTime(restaurantRequestDTO.getRestaurantDTO().getOpenTime());
        restaurant.get().setCloseTime(restaurantRequestDTO.getRestaurantDTO().getCloseTime());

        return restaurantRepository.save(restaurant.get());
    }

    public void deleteRestaurant(String userName, Long restaurantId) {
        log.info("RestaruantOwnerService: addRestaurant");
        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        log.info("User: {}", user);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not logged in");
        }
        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        log.info("Restaurant owner: {}", owner);
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        log.info("Restaurant: {}", restaurant);
        if(restaurant.isEmpty()) {
            throw new IllegalStateException("Restaurant not found");
        }

        restaurantRepository.delete(restaurant.get());
    }
}
