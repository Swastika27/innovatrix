package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.dto.LocationDTO;
import com.innovatrix.ahaar.dto.RestaurantSearchDTO;
import com.innovatrix.ahaar.exception.ResourceNotFoundException;
import com.innovatrix.ahaar.model.FoodItem;
import com.innovatrix.ahaar.model.Restaurant;
import com.innovatrix.ahaar.repository.FoodItemRepository;
import com.innovatrix.ahaar.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class RestaurantCustomerSideService {

    public static final double NEARBY_DISTANCE = 10000;
    public static final double MAX_DISTANCE = 15000;

    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;


    @Autowired
    public RestaurantCustomerSideService(RestaurantRepository restaurantRepository, FoodItemRepository foodItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.foodItemRepository = foodItemRepository;
    }

    public Restaurant getRestaurantById(Long id) throws ResourceNotFoundException {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if (restaurant.isEmpty()) {
            throw new ResourceNotFoundException("Restaurant");
        }
        return restaurant.get();
    }

    public Set<FoodItem> getItemsRestaurant(Long restaurantId) throws ResourceNotFoundException {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isEmpty()) {
            throw new ResourceNotFoundException("restaurant");
        }
        return restaurant.get().getMenu();
    }

    public Set<Restaurant> findNearbyRestaurants(LocationDTO location) {
        return restaurantRepository.findNearByRestaurants(location.getLatitude(), location.getLongitude(), NEARBY_DISTANCE);
    }

    public Set<Restaurant> findRestaurants(RestaurantSearchDTO searchDTO) {
        return restaurantRepository.findRestaurants(searchDTO.getLocation().getLatitude(), searchDTO.getLocation().getLongitude(), MAX_DISTANCE, searchDTO.getCuisine(), searchDTO.getItemName(), searchDTO.getPrice());
    }

    public FoodItem getItemDetails(Long restaurantId, Long itemId) throws ResourceNotFoundException {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isEmpty()) {
            throw new ResourceNotFoundException("restaurant");
        }

        Optional<FoodItem> foodItem = foodItemRepository.findById(itemId);
        if (foodItem.isEmpty()) {
            throw new ResourceNotFoundException("foodItem");
        }

        if (!restaurant.get().getId().equals(foodItem.get().getRestaurant().getId())) {
            throw new IllegalStateException("Requested item does not belong to restaurant");
        }
        return foodItem.get();
    }
}
