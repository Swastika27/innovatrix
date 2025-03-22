package com.innovatrix.ahaar.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantSearchDTO {
    LocationDTO location;
    double price;
    String cuisine;
    String itemName;
}
