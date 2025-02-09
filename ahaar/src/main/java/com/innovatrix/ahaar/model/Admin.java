package com.innovatrix.ahaar.model;

import jakarta.persistence.*;

@Entity
public class Admin extends ApplicationUser {
    @Id
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private ApplicationUser user;

}
