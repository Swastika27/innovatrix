package com.innovatrix.ahaar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantRequestDTO {
    RestaurantDTO restaurantDTO;
    LocationDTO locationDTO;
}
