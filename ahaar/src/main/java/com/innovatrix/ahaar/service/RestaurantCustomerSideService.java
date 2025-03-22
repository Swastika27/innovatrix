package com.innovatrix.ahaar.service;

import com.innovatrix.ahaar.dto.LocationDTO;
import com.innovatrix.ahaar.dto.RestaurantSearchDTO;
import com.innovatrix.ahaar.model.FoodItem;
import com.innovatrix.ahaar.model.Restaurant;
import com.innovatrix.ahaar.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RestaurantCustomerSideService {
    public static final double NEARBY_DISTANCE = 10000;
    public static final double MAX_DISTANCE = 15000;
    private final RestaurantRepository restaurantRepository;


    @Autowired
    public RestaurantCustomerSideService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant getRestaurantById(int id) {
        return null;
    }

    public HashSet<FoodItem> getItemsRestaurant(Long restaurantId) {
        return null;
    }

    public Set<Restaurant> findNearbyRestaurants(LocationDTO location) {
        return restaurantRepository.findNearByRestaurants(location.getLatitude(), location.getLongitude(), NEARBY_DISTANCE);
    }

    public Set<Restaurant> findRestaurants(RestaurantSearchDTO searchDTO) {
        return restaurantRepository.findRestaurants(searchDTO.getLocation().getLatitude(), searchDTO.getLocation().getLongitude(), MAX_DISTANCE, searchDTO.getCuisine(), searchDTO.getItemName(), searchDTO.getPrice());
    }
}
