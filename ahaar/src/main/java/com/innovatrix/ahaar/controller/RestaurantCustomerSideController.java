package com.innovatrix.ahaar.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ahaar/customer/restaurants")
public class RestaurantCustomerSideController {

    @GetMapping("/nearby")
    // get nearby restaurants

    @GetMapping("/{restaurantId}")
    // get a specific restaurant

    @GetMapping("/{restaurantId}/items")
    // get all items of a restaurant

    @GetMapping("/{restaurantId}/items/{itemId}")

    @GetMapping("/filtered")
    // search


}
