package com.innovatrix.ahaar.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.sql.Time;
import java.util.Set;

@Table(name = "Restaurant")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Restaurant {
    @Id
    @SequenceGenerator(
            name = "restaurant_id_sequence",
            sequenceName = "restaurant_id_sequence",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "restaurant_id_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private RestaurantOwner owner;

    private String name;
    private String contactNumber;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point location; // Stores latitude & longitude

    private String cuisine;

    @OneToMany
    @JoinColumn(name = "restaurant_id")
    private Set<FoodItem> menu;

    private Time openTime;
    private Time closeTime;
}
