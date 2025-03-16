package com.innovatrix.ahaar.repository;

import com.innovatrix.ahaar.model.Restaurant;
import com.innovatrix.ahaar.model.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    ArrayList<Restaurant> findRestaurantsByOwner(RestaurantOwner owner);
}

