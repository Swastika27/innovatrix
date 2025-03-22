package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.dto.FoodItemDTO;
import com.innovatrix.ahaar.dto.RestaurantRequestDTO;
import com.innovatrix.ahaar.exception.ResourceNotFoundException;
import com.innovatrix.ahaar.exception.UnauthorizedActionException;
import com.innovatrix.ahaar.exception.UserNotFoundException;
import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.FoodItem;
import com.innovatrix.ahaar.model.Restaurant;
import com.innovatrix.ahaar.model.RestaurantOwner;
import com.innovatrix.ahaar.repository.FoodItemRepository;
import com.innovatrix.ahaar.repository.RestaurantOwnerRepository;
import com.innovatrix.ahaar.repository.RestaurantRepository;
import com.innovatrix.ahaar.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class RestaurantOwnerSideService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private final FoodItemRepository foodItemRepository;
    private final LocationService locationService;

    @Autowired
    public RestaurantOwnerSideService(RestaurantRepository restaurantRepository, UserRepository userRepository, RestaurantOwnerRepository restaurantOwnerRepository, FoodItemRepository foodItemRepository, LocationService locationService) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.foodItemRepository = foodItemRepository;
        this.locationService = locationService;
    }

    public Restaurant addRestaurant(String userName, RestaurantRequestDTO requestDTO) throws UnauthorizedActionException {
        log.info("RestaurantOwnerService: addRestaurant");
        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        if (user.isEmpty()) {
            throw new UnauthorizedActionException();
        }
        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        if(requestDTO.getLocationDTO() == null) {
            throw new IllegalArgumentException("Location is required");
        }

        Point location = locationService.createPoint(requestDTO.getLocationDTO().getLatitude(), requestDTO.getLocationDTO().getLongitude());
        Restaurant restaurant = requestDTO.getRestaurantDTO().toRestaurant(owner.get(), location);
        owner.get().addRestaurant(restaurant);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public Restaurant updateRestaurant(String userName, Long restaurantId, RestaurantRequestDTO restaurantRequestDTO) throws UnauthorizedActionException {
        log.info("RestaurantOwnerService: updateRestaurant");

        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not logged in");
        }
        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if(restaurant.isEmpty()) {
            throw new IllegalStateException("Restaurant not found");
        }

        if(!Objects.equals(restaurant.get().getOwner().getId(), owner.get().getId())) {
            throw new UnauthorizedActionException();
        }

        restaurant.get().setName(restaurantRequestDTO.getRestaurantDTO().getName());
        restaurant.get().setContactNumber(restaurantRequestDTO.getRestaurantDTO().getContactNumber());
//        Point location = locationService.createPoint(restaurantRequestDTO.getLocationDTO().getLatitude(), restaurantRequestDTO.getLocationDTO().getLongitude());
//        restaurant.get().setLocation(location);
        restaurant.get().setCuisine(restaurantRequestDTO.getRestaurantDTO().getCuisine());
        restaurant.get().setOpenTime(restaurantRequestDTO.getRestaurantDTO().getOpenTime());
        restaurant.get().setCloseTime(restaurantRequestDTO.getRestaurantDTO().getCloseTime());

        return restaurantRepository.save(restaurant.get());
    }

    public void deleteRestaurant(String userName, Long restaurantId) throws UnauthorizedActionException {
        log.info("RestaurantOwnerService: deleteRestaurant");
        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not logged in");
        }
        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if(restaurant.isEmpty()) {
            throw new IllegalStateException("Restaurant not found");
        }

        if(!Objects.equals(restaurant.get().getOwner().getId(), owner.get().getId())) {
            throw new UnauthorizedActionException();
        }

        restaurantRepository.delete(restaurant.get());
    }

    public void addFoodItem(String userName, Long restaurantId, FoodItemDTO foodItemDTO) throws ResourceNotFoundException, UnauthorizedActionException {
        log.info("RestaruantOwnerService: addFoodItem");

        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not logged in");
        }

        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if(restaurant.isEmpty()) {
            throw new ResourceNotFoundException("restaurant");
        }

        if(!Objects.equals(restaurant.get().getOwner().getId(), owner.get().getId())) {
            throw new UnauthorizedActionException();
        }

        if(foodItemDTO.getServing() <=0 || foodItemDTO.getPrice() <= 0) {
            throw new IllegalArgumentException("Serving and price must be greater than 0");
        }

        FoodItem item = foodItemDTO.toFoodItem();
        item.setRestaurant(restaurant.get());
        foodItemRepository.save(item);
        restaurant.get().addItem(item);
        restaurantRepository.save(restaurant.get());
    }

    @Transactional
    public void editFoodItem(String userName, Long restaurantId, Long itemId, FoodItemDTO foodItemDTO) throws ResourceNotFoundException, UnauthorizedActionException {
        log.info("RestaurantOwnerService: editFoodItem");

        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not logged in");
        }

        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if(restaurant.isEmpty()) {
            throw new ResourceNotFoundException("restaurant");
        }

        Optional<FoodItem> item = foodItemRepository.findById(itemId);
        if(item.isEmpty()) {
            throw new ResourceNotFoundException("item");
        }

        if(!Objects.equals(restaurant.get().getOwner().getId(), owner.get().getId())) {
            throw new UnauthorizedActionException();
        }

        item.get().setName(foodItemDTO.getName());
        item.get().setDescription(foodItemDTO.getDescription());
        item.get().setPrice(foodItemDTO.getPrice());
        item.get().setServing(foodItemDTO.getServing());
        item.get().setAvailable(foodItemDTO.isAvailable());
        item.get().setImageUrl(foodItemDTO.getImageUrl());

        foodItemRepository.save(item.get());
    }

    public void deleteFoodItem(String userName, Long restaurantId, Long itemId) throws ResourceNotFoundException, UnauthorizedActionException {
        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        log.info("User: {}", user);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not logged in");
        }

        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if(restaurant.isEmpty()) {
            throw new ResourceNotFoundException("restaurant");
        }

        Optional<FoodItem> item = foodItemRepository.findById(itemId);
        if(item.isEmpty()) {
            throw new ResourceNotFoundException("item");
        }

        if(!Objects.equals(restaurant.get().getOwner().getId(), owner.get().getId())) {
            throw new UnauthorizedActionException();
        }

        restaurant.get().removeItem(item.get());
        foodItemRepository.delete(item.get());
    }

    public Set<FoodItem> getAllItems(Long restaurantId) throws ResourceNotFoundException {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if(restaurant.isEmpty()) {
            throw new ResourceNotFoundException("restaurant");
        }

        return restaurant.get().getMenu();
    }

    public Set<Restaurant> getAllRestaurantsOwner(String userName) {
        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not logged in");
        }

        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        return owner.get().getRestaurants();
    }

    public Restaurant getRestaurant(String userName, Long restaurantId) throws ResourceNotFoundException, UnauthorizedActionException {
        Optional<ApplicationUser> user = userRepository.findByUserName(userName);
        if (user.isEmpty()) {
            throw new IllegalStateException("User not logged in");
        }

        Optional<RestaurantOwner> owner = restaurantOwnerRepository.findById(user.get().getId());
        if (owner.isEmpty()) {
            throw new UserNotFoundException(user.get().getId());
        }

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if(restaurant.isEmpty()) {
            throw new ResourceNotFoundException("restaurant");
        }
        if(!Objects.equals(restaurant.get().getOwner().getId(), owner.get().getId())) {
            throw new UnauthorizedActionException();
        }

        return restaurant.get();
    }

}
