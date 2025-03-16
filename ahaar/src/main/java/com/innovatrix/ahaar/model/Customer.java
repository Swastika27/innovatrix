package com.innovatrix.ahaar.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder

public class Customer {
    @Id
    @SequenceGenerator(
            name = "customer_id_sequence",
            sequenceName = "customer_id_sequence",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_id_sequence"
    )
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private ApplicationUser user;

    private Date dateOfBirth;
    private String name;
    private String currentAddress;
    private String homeTown;
    private String phoneNumber;
    private String educationalInstitution;
    private String currentWorkPlace;
    @Enumerated(EnumType.STRING)
    private Gender gender;

}
