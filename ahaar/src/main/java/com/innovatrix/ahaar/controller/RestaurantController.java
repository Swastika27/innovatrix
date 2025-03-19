package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.dto.RestaurantRequestDTO;
import com.innovatrix.ahaar.model.APIResponse;
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

@RestController()
@RequestMapping("/ahaar/restaurants")
public class RestaurantController {

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
    public ResponseEntity<APIResponse<Void>> deleteRestaurant(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long restaurantId, BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        restaurantOwnerService.deleteRestaurant(userDetails.getUsername(), restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurant deleted successfully", null));
    }

//    @GetMapping("{restaurantId}/items")
//    // get all items of the restaurant
//
//    @PostMapping("{restaurantId}/items")
//    // add a new item to restaurant
//
//    @PutMapping("{restaurantId}/items/{itemId}")
//    // edit an item
//
//    @DeleteMapping("{restaurantId}/items/{itemId}")
//    // delete an item
}
