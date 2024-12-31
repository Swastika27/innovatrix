package com.innovatrix.ahaar.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class MyUser extends Auditable<String> implements Serializable {
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
			nullable = false
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
	private String password;

	public MyUser(String userName, String email, String password) {
		this.userName = userName;
		this.email = email;
		this.password = password;
	}
}