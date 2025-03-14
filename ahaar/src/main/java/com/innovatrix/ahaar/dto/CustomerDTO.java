package com.innovatrix.ahaar.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovatrix.ahaar.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@RequiredArgsConstructor
public class CustomerDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private Date dateOfBirth;
    private String name;
    private String currentAddress;
    private String homeTown;
    private String phoneNumber;
    private String educationalInstitution;
    private String currentWorkPlace;
    private Gender gender;
}
