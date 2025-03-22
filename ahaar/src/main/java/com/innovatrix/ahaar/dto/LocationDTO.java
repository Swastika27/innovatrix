package com.innovatrix.ahaar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    @jakarta.validation.constraints.DecimalMin(value = "-90.0", message = "Latitude must be ≥ -90")
    @jakarta.validation.constraints.DecimalMax(value = "90.0", message = "Latitude must be ≤ 90")
    private double latitude;

    @jakarta.validation.constraints.DecimalMin(value = "-180.0", message = "Longitude must be ≥ -180")
    @jakarta.validation.constraints.DecimalMax(value = "180.0", message = "Longitude must be ≤ 180")
    private double longitude;
}
