package com.innovatrix.ahaar.dto;

import com.innovatrix.ahaar.model.Restaurant;
import com.innovatrix.ahaar.model.RestaurantOwner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;

import java.sql.Time;

@Slf4j
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private String name;

    private String contactNumber;

    private String cuisine;

    private Time openTime;
    private Time closeTime;

    public Restaurant toRestaurant(RestaurantOwner owner, Point point) {

        log.info("RestaurantDTO toRestaurant");

        return Restaurant.builder()
                .owner(owner)
                .name(name)
                .contactNumber(contactNumber)
                .location(point)
                .cuisine(cuisine)
                .openTime(openTime)
                .closeTime(closeTime)
                .build();
    }
}
