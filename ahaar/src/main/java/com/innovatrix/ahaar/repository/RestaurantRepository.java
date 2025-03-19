package com.innovatrix.ahaar.repository;

import com.innovatrix.ahaar.model.Restaurant;
import com.innovatrix.ahaar.model.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    ArrayList<Restaurant> findRestaurantsByOwner(RestaurantOwner owner);

    Optional<Restaurant> findRestaurantById(Long id);
}

