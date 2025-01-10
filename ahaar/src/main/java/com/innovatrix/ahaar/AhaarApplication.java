package com.innovatrix.ahaar;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
public class AhaarApplication {

	public static void main(String[] args) {
		SpringApplication.run(AhaarApplication.class, args);
	}


}
