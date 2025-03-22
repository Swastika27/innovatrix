package com.innovatrix.ahaar.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RestaurantOwner {
    @Id
    @SequenceGenerator(
            name = "restaurant_owner_id_sequence",
            sequenceName = "restaurant_owner_id_sequence",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "restaurant_owner_id_sequence"
    )
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private ApplicationUser user;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false,
            unique = true)
    private String phoneNumber;
    @Column(nullable = false,
            unique = true)
    private String NID;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Restaurant> restaurants;

    public RestaurantOwner(ApplicationUser applicationUser, @NotBlank(message = "Name is required") @Size(min = 4, message = "Name must be at least 4 characters") String name, @NotBlank(message = "Phone number is required") String phoneNumber, @NotBlank(message = "NID is required") String nid) {
        this.user = applicationUser;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.NID = nid;
        this.restaurants = new HashSet<>();
    }

    public Restaurant addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
        return restaurant;
    }
}
