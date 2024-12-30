package com.innovatrix.ahaar;

import com.innovatrix.ahaar.model.MyUser;
import com.innovatrix.ahaar.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class AhaarApplication {

	public static void main(String[] args) {
		SpringApplication.run(AhaarApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserRepository userRepository) {
		return args -> {
			MyUser myUser1 = new MyUser("SwastikaPandit",
					"swastikapandit@gmail.com",
					"abcdef");
			MyUser myUser2 = new MyUser("FairuzMubashwera",
					"fairuz@gmail.com",
					"12345");
			MyUser myUser3 = new MyUser("MetalyKhatun",
					"metaly@gmail.com",
					"12345");

			userRepository.saveAll(List.of(myUser1, myUser2, myUser3));
		};
	}
}
