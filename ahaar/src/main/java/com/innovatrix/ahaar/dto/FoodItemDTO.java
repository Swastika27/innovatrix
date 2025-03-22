package com.innovatrix.ahaar.dto;

import com.innovatrix.ahaar.model.FoodItem;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemDTO {
    @NotNull(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    private double price;

    private int serving;

    @NotNull(message = "Availability is required")
    private boolean available;

    private String imageUrl;

    public FoodItem toFoodItem() {
        return FoodItem.builder().
                name(this.name)
                .description(this.description)
                .price(this.price)
                .serving(this.serving)
                .available(available)
                .imageUrl(imageUrl != null ? imageUrl : "")
                .build();
    }
}
