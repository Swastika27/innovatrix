package com.innovatrix.ahaar.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantSearchDTO {
    @NotNull(message = "Location cannot be null")
    private LocationDTO location;

    private double price;
    private String cuisine;
    private String itemName;
}
