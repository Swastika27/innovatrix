package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.dto.FoodItemDTO;
import com.innovatrix.ahaar.dto.RestaurantRequestDTO;
import com.innovatrix.ahaar.exception.ResourceNotFoundException;
import com.innovatrix.ahaar.exception.UnauthorizedActionException;
import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.model.FoodItem;
import com.innovatrix.ahaar.model.Restaurant;
import com.innovatrix.ahaar.service.RestaurantOwnerSideService;
import com.innovatrix.ahaar.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController()
@RequestMapping("/ahaar/owner/restaurants")
public class RestaurantOwnerSideController {

    @Autowired
    private RestaurantOwnerSideService restaurantOwnerSideService;

    @GetMapping
//     get all restaurants of this owner
    public ResponseEntity<APIResponse<Set<Restaurant>>> getAllRestaurantsByOwner(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurants retrieved successfully", restaurantOwnerSideService.getAllRestaurantsOwner(userDetails.getUsername())));
    }

    @GetMapping("/{restaurantId}")
    // get single
public ResponseEntity<APIResponse<Restaurant>> getRestaurant(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long restaurantId) throws ResourceNotFoundException, UnauthorizedActionException {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurant retrived successfully", restaurantOwnerSideService.getRestaurant(userDetails.getUsername(), restaurantId)));
    }

    // add new restaurant
    @PostMapping()
    public ResponseEntity<APIResponse<Restaurant>> addRestaurant(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RestaurantRequestDTO restaurantRequestDTO, BindingResult bindingResult) throws BindException, UnauthorizedActionException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            Restaurant newRestaurant = restaurantOwnerSideService.addRestaurant(userDetails.getUsername(), restaurantRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseBuilder.success(HttpStatus.CREATED.value(), "Restaurant added successfully", newRestaurant));
        }
    }

    // edit -> only owner
    @PutMapping("{restaurantId}")
    public ResponseEntity<APIResponse<Restaurant>> editRestaurantData(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RestaurantRequestDTO restaurantRequestDTO, @PathVariable Long restaurantId, BindingResult bindingResult) throws BindException, UnauthorizedActionException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Restaurant restaurant = restaurantOwnerSideService.updateRestaurant(userDetails.getUsername(), restaurantId, restaurantRequestDTO);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurant updated successfully", restaurant));
    }

    @DeleteMapping("{restaurantId}")
    public ResponseEntity<APIResponse<Void>> deleteRestaurant(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long restaurantId) throws BindException, UnauthorizedActionException {

        restaurantOwnerSideService.deleteRestaurant(userDetails.getUsername(), restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurant deleted successfully", null));
    }

    @GetMapping("/{restaurantId}/items")
    // get all items of the restaurant
    public ResponseEntity<APIResponse<Set<FoodItem>>> getRestaurantAllItems(@PathVariable Long restaurantId) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurant all items retrieved successfully", restaurantOwnerSideService.getAllItems(restaurantId)));
    }

    @PostMapping("/{restaurantId}/items")
    // add a new item to restaurant
    public ResponseEntity<APIResponse<Void>> addItemToRestaurant(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long restaurantId, @RequestBody FoodItemDTO foodItemDTO, BindingResult bindingResult) throws BindException, ResourceNotFoundException, UnauthorizedActionException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        restaurantOwnerSideService.addFoodItem(userDetails.getUsername(), restaurantId, foodItemDTO);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ResponseBuilder.success(HttpStatus.OK.value(), "Item added successfully", null));
    }

    //
    @PutMapping("/{restaurantId}/items/{itemId}")
    // edit an item
    public ResponseEntity<APIResponse<Void>> editItem(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long restaurantId, @PathVariable Long itemId, @RequestBody FoodItemDTO foodItemDTO, BindingResult bindingResult) throws BindException, ResourceNotFoundException, UnauthorizedActionException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        restaurantOwnerSideService.editFoodItem(userDetails.getUsername(), restaurantId, itemId, foodItemDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Item updated successfully", null));
    }

    @DeleteMapping("/{restaurantId}/items/{itemId}")
    // delete an item
    public ResponseEntity<APIResponse<Void>> deleteItem(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long restaurantId, @PathVariable Long itemId) throws BindException, ResourceNotFoundException, UnauthorizedActionException {

        restaurantOwnerSideService.deleteFoodItem(userDetails.getUsername(), restaurantId, itemId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Item deleted successfully", null));
    }
}
