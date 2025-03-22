package com.innovatrix.ahaar.repository;

import com.innovatrix.ahaar.model.Restaurant;
import com.innovatrix.ahaar.model.RestaurantOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    ArrayList<Restaurant> findRestaurantsByOwner(RestaurantOwner owner);

    Optional<Restaurant> findRestaurantById(Long id);

    @Query(value = """
                    select * from restaurant
                    where ST_DWithin(restaurant.location, ST_setSRID(ST_makePoint(:longitude, :latitude), 4326), :radius)
            """, nativeQuery = true)
    Set<Restaurant> findNearByRestaurants(double latitude, double longitude, double radius);

    @Query(value = """
                SELECT  r.id as id,
                        r.name as name,
                        r.cuisine as cuisine,
                        r.open_time as open_time,
                        r.close_time as close_time,
                        r.location as location,
                        r.contact_number as contact_number,
                        r.owner_id as owner_id,
                        f.id as item_id,
                        f.name as item_name,
                        f.price as item_price
                        FROM restaurant r
                LEFT JOIN food_item f ON f.restaurant_id = r.id
WHERE ST_DWithin(r.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :radius)
                AND (:cuisine IS NULL OR r.cuisine ILIKE :cuisine)
                AND (:itemName IS NULL OR f.name ILIKE CONCAT('%', :itemName, '%'))
                AND (:price IS NULL OR f.price <= :price)
            """, nativeQuery = true)
    Set<Restaurant> findRestaurants(double latitude, double longitude, double radius, String cuisine, String itemName, double price);
}



//


