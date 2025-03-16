package com.innovatrix.ahaar.controller;


import com.innovatrix.ahaar.dto.CustomerDTO;
import com.innovatrix.ahaar.dto.JwtResponseDTO;
import com.innovatrix.ahaar.dto.LoginDTO;
import com.innovatrix.ahaar.dto.RefreshTokenRequestDTO;
import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.model.Customer;
import com.innovatrix.ahaar.model.RefreshToken;
import com.innovatrix.ahaar.service.CustomerService;
import com.innovatrix.ahaar.service.RefreshTokenService;
import com.innovatrix.ahaar.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ahaar/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final RefreshTokenService refreshTokenService;

    public CustomerController(CustomerService customerService, RefreshTokenService refreshTokenService) {
        this.customerService = customerService;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/")
    public ResponseEntity<APIResponse<Page<Customer>>> getUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Customer> allUsers = customerService.getAll(page, size);

        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "Customer retrieved successfully", allUsers));
    }

    @GetMapping("{id}")
    public ResponseEntity<APIResponse<Optional<Customer>>> getById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getUserById(id);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "Customer retrieved successfully", customer));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<APIResponse<Customer>> updateDetails(@PathVariable("id") Long customerId, @RequestBody Customer customer) {

        customerService.updateInfo(customerId, customer);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "Customer updated successfully", customer));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<APIResponse<Customer>> deleteCustomer(@PathVariable("id") Long customerId) {
        customerService.deleteUser(customerId);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "customer deleted successfully", null));
    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<Optional<Customer>>> signUp(@Valid @RequestBody CustomerDTO customerDTO, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Optional<Customer> newCustomer = customerService.add(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseBuilder.success(HttpStatus.CREATED.value(), "Customer added successfully", newCustomer));

    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<JwtResponseDTO>> login(@RequestBody LoginDTO loginDTO) {

        try {
            String jwtToken = customerService.login(loginDTO);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginDTO.getUsername());
            JwtResponseDTO jwtResponseDTO = JwtResponseDTO.builder().accessToken(jwtToken).refreshToken(refreshToken.getToken()).build();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseBuilder.success(HttpStatus.OK.value(), "Customer login successfully", jwtResponseDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), "Authentication failed", null));
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<APIResponse<JwtResponseDTO>> refreshToken(@RequestBody RefreshTokenRequestDTO refreshToken) {
        JwtResponseDTO jwtResponseDTO = customerService.refresh(refreshToken.getRefreshToken());

        if (jwtResponseDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token", null));
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseBuilder.success(HttpStatus.OK.value(), "Customer refresh successful", jwtResponseDTO)
        );
    }
}
