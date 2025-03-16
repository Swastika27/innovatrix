package com.innovatrix.ahaar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innovatrix.ahaar.dto.ApplicationUserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(exclude = "password")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class ApplicationUser extends Auditable<String> implements Serializable {
    @Id
    @SequenceGenerator(
            name = "user_id_sequence",
            sequenceName = "user_id_sequence",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_id_sequence"
    )
    private Long id;

    @Column(
            name = "user_name",
            nullable = false,
            unique = true
    )
    private String userName;

    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    private String email;

    @Column(
            name = "password",
            nullable = false
    )
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public ApplicationUser(String userName, String email, String password, Role role) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public ApplicationUserDTO toDTO() {
        return new ApplicationUserDTO(this.userName, this.email, this.password, this.role);
    }
}