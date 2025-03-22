package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.dto.LocationDTO;
import com.innovatrix.ahaar.dto.RestaurantSearchDTO;
import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.model.Restaurant;
import com.innovatrix.ahaar.model.RestaurantOwner;
import com.innovatrix.ahaar.service.RestaurantCustomerSideService;
import com.innovatrix.ahaar.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/ahaar/customer/restaurants")
public class RestaurantCustomerSideController {

    private RestaurantCustomerSideService restaurantCustomerSideService;

    @Autowired
    public RestaurantCustomerSideController(RestaurantCustomerSideService restaurantCustomerSideService) {
        this.restaurantCustomerSideService = restaurantCustomerSideService;
    }

    @GetMapping("/nearby")
    // get nearby restaurants
    public ResponseEntity<APIResponse<Set<Restaurant>>> findNearbyRestaurants(@RequestBody LocationDTO location) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurants retrieved successfully", restaurantCustomerSideService.findNearbyRestaurants(location)));
    }

    @GetMapping("/search")
    // search
    public ResponseEntity<APIResponse<Set<Restaurant>>> findRestaurants(@RequestBody RestaurantSearchDTO searchDTO) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurants retrieved successfully", restaurantCustomerSideService.findRestaurants(searchDTO)));
    }

//    @GetMapping("/{restaurantId}")
//    // get a specific restaurant
//
//    @GetMapping("/{restaurantId}/items")
//    // get all items of a restaurant
//
//    @GetMapping("/{restaurantId}/items/{itemId}")
//    // get details of an item
//


}
