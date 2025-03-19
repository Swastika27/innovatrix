package com.innovatrix.ahaar.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
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

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int serving;

    @Column(nullable = false)
    private boolean available;

    @Column(nullable = false)
    private String imageUrl;
}
