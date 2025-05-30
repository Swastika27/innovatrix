package com.innovatrix.ahaar.repository;

import com.innovatrix.ahaar.model.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RestaurantOwnerRepository extends JpaRepository<RestaurantOwner, Long> {
}
