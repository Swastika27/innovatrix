package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.DTO.JwtResponseDTO;
import com.innovatrix.ahaar.DTO.LoginDTO;
import com.innovatrix.ahaar.DTO.RefreshTokenRequestDTO;
import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.model.RefreshToken;
import com.innovatrix.ahaar.model.RestaurantOwner;
import com.innovatrix.ahaar.service.CustomerService;
import com.innovatrix.ahaar.service.RefreshTokenService;
import com.innovatrix.ahaar.service.RestaurantOwnerService;
import com.innovatrix.ahaar.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ahaar/restaurant-owner")
public class RestaurantOwnerController {

    private final RestaurantOwnerService restaurantOwnerService;
    private final RefreshTokenService refreshTokenService;
    private final CustomerService customerService;

    @Autowired
    public RestaurantOwnerController(RestaurantOwnerService restaurantOwnerService, RefreshTokenService refreshTokenService, CustomerService customerService) {
        this.restaurantOwnerService = restaurantOwnerService;
        this.refreshTokenService = refreshTokenService;
        this.customerService = customerService;
    }

    @GetMapping("/")
    public ResponseEntity<APIResponse<Page<RestaurantOwner>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Page<RestaurantOwner> allRestaurantOwners = restaurantOwnerService.getAll(page, size);

        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurant Owners", allRestaurantOwners));
    }

    @GetMapping("{id}")
    public ResponseEntity<APIResponse<RestaurantOwner>> getById(@PathVariable Long id){
        RestaurantOwner restaurantOwner = restaurantOwnerService.getById(id);

        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "restaurant owner retrieved successfylly", restaurantOwner));
    }

    @PutMapping("{id}")
    public ResponseEntity<APIResponse<RestaurantOwner>> update(@PathVariable int id, @RequestBody RestaurantOwner restaurantOwner){

       return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "Information updated successfully", restaurantOwnerService.update(restaurantOwner)));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<APIResponse<RestaurantOwner>> delete(@PathVariable Long id){
        restaurantOwnerService.delete(id);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "Restaurant owner deleted successfully", null));
    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<RestaurantOwner>> signup(@Valid @RequestBody RestaurantOwner restaurantOwner, BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        RestaurantOwner newRestaurantOwner = restaurantOwnerService.add(restaurantOwner);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseBuilder.success((HttpStatus.CREATED.value()), "Restaurant owner created successfully", newRestaurantOwner));
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<JwtResponseDTO>> login(@Valid @RequestBody LoginDTO loginDTO){
        try {
            String jwtToken = restaurantOwnerService.login(loginDTO);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginDTO.getUsername());
            JwtResponseDTO jwtResponseDTO = JwtResponseDTO.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken.getToken()).build();

            return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "logged in successfully", jwtResponseDTO))    ;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), "Authentication failed", null));
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<APIResponse<JwtResponseDTO>> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        JwtResponseDTO jwtResponseDTO = customerService.refresh(refreshTokenRequestDTO.getRefreshToken());
        if(jwtResponseDTO == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token", null));
        }

        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "JWT token generated successfully", jwtResponseDTO));
    }


}
