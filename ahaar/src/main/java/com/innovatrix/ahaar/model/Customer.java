package com.innovatrix.ahaar.model;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends ApplicationUser {
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
