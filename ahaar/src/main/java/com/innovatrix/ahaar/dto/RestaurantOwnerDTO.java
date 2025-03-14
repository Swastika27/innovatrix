package com.innovatrix.ahaar.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovatrix.ahaar.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantOwnerDTO {
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

    @NotBlank(message = "Name is required")
    @Size(min = 4, message = "Name must be at least 4 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String name;

    @NotBlank(message = "Phone number is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String phoneNumber;

    @NotBlank(message = "NID is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String NID;
}
