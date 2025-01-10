package com.innovatrix.ahaar.model;

import lombok.Data;

@Data
public class ApplicationUserDTO {
    String userName;
    String email;
    String password;

    public ApplicationUserDTO(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public ApplicationUser toEntity () {
        return new ApplicationUser(this.userName, this.email, this.password);
    }

}
