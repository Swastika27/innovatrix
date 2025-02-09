package com.innovatrix.ahaar.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer  {
    @Id
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
    private String email;
    private String educationalInstitution;
    private String currentWorkPlace;
    private Gender gender;

}
