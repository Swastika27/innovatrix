package com.innovatrix.ahaar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "food_item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItem {
    @Id
    @SequenceGenerator(
            name = "food_item_id_sequence",
            sequenceName = "food_item_id_sequence",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "food_item_id_sequence"
    )
    private Long id;

    private String name;
    private String description;
    private double price;
    private int serving;
    private boolean available;
    private String imageUrl;
}
