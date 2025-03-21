package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.dto.FoodItemDTO;
import com.innovatrix.ahaar.dto.RestaurantRequestDTO;
import com.innovatrix.ahaar.exception.ResourceNotFoundException;
import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.model.FoodItem;
import com.innovatrix.ahaar.model.Restaurant;
import com.innovatrix.ahaar.service.RestaurantOwnerService;
import com.innovatrix.ahaar.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController()
@RequestMapping("/ahaar/owner/restaurants")
public class RestaurantOwnerSideController {

    @Autowired
    private RestaurantOwnerService restaurantOwnerService;

//    @GetMapping
    // get all -> only admin can access it

//    @GetMapping
    // get single

    // add new restaurant
    @PostMapping()
    public ResponseEntity<APIResponse<Restaurant>> addRestaurant(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RestaurantRequestDTO restaurantRequestDTO, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            Restaurant newRestaurant = restaurantOwnerService.addRestaurant(userDetails.getUsername(), restaurantRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseBuilder.success(HttpStatus.CREATED.value(), "Restaurant added successfully", newRestaurant));
        }
    }

    // edit -> only owner
    @PutMapping("{restaurantId}")
    public ResponseEntity<APIResponse<Restaurant>> editRestaurantData(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RestaurantRequestDTO restaurantRequestDTO, @PathVariable Long restaurantId, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Restaurant restaurant = restaurantOwnerService.updateRestaurant(userDetails.getUsername(), restaurantId, restaurantRequestDTO);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurant updated successfully", restaurant));
    }

    @DeleteMapping("{restaurantId}")
    public ResponseEntity<APIResponse<Void>> deleteRestaurant(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long restaurantId) throws BindException {

        restaurantOwnerService.deleteRestaurant(userDetails.getUsername(), restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurant deleted successfully", null));
    }

    @GetMapping("/{restaurantId}/items")
    // get all items of the restaurant
    public ResponseEntity<APIResponse<Set<FoodItem>>> getRestaurantAllItems(@PathVariable Long restaurantId) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurant all items retrieved successfully", restaurantOwnerService.getAllItems(restaurantId)));
    }
    
    @PostMapping("/{restaurantId}/items")
    // add a new item to restaurant
    public ResponseEntity<APIResponse<Void>> addItemToRestaurant (@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long restaurantId, @RequestBody FoodItemDTO foodItemDTO, BindingResult bindingResult) throws BindException, ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        restaurantOwnerService.addFoodItem(userDetails.getUsername(), restaurantId, foodItemDTO);
        return ResponseEntity.status(HttpStatus.OK.value()).body(ResponseBuilder.success(HttpStatus.OK.value(), "Item added successfully", null));
    }
//
    @PutMapping("/{restaurantId}/items/{itemId}")
    // edit an item
    public ResponseEntity<APIResponse<Void>> editItem(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long restaurantId, @PathVariable Long itemId, @RequestBody FoodItemDTO foodItemDTO, BindingResult bindingResult) throws BindException, ResourceNotFoundException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        restaurantOwnerService.editFoodItem(userDetails.getUsername(), restaurantId, itemId, foodItemDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Item updated successfully", null));
    }

    @DeleteMapping("/{restaurantId}/items/{itemId}")
    // delete an item
    public ResponseEntity<APIResponse<Void>> deleteItem(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long restaurantId, @PathVariable Long itemId) throws BindException, ResourceNotFoundException {

        restaurantOwnerService.deleteFoodItem(userDetails.getUsername(), restaurantId, itemId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Item deleted successfully", null));
    }
}
