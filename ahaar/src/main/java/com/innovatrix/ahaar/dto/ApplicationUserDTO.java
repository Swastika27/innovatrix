package com.innovatrix.ahaar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonInclude;


@Setter
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationUserDTO {

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

    @NotNull(message = "Role is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Role role;

    public ApplicationUser toEntity () {
        return new ApplicationUser(this.userName, this.email, this.password, this.role);
    }

}
