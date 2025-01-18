package com.innovatrix.ahaar.repository;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findByEmail(String email);
}
