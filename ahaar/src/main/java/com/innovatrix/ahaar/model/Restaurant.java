package com.innovatrix.ahaar.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.sql.Time;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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
    @JsonBackReference
    private RestaurantOwner owner;

    @Column(nullable = false)
    private String name;

    private String contactNumber;

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point location; // Stores latitude & longitude

    private String cuisine;

    @OneToMany
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Set<FoodItem> menu;

    private Time openTime;
    private Time closeTime;

    public Restaurant(RestaurantOwner owner, String name, String contactNumber, Point location, String cuisine) {
        this.owner = owner;
        this.name = name;
        this.contactNumber = contactNumber;
        this.location = location;
        this.cuisine = cuisine;
        this.menu = new HashSet<>();
    }
}
