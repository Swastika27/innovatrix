package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.DTO.JwtResponseDTO;
import com.innovatrix.ahaar.DTO.RefreshTokenRequestDTO;
import com.innovatrix.ahaar.exception.UserNotFoundException;
import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.DTO.ApplicationUserDTO;
import com.innovatrix.ahaar.DTO.LoginDTO;
import com.innovatrix.ahaar.model.RefreshToken;
import com.innovatrix.ahaar.service.JWTService;
import com.innovatrix.ahaar.service.RefreshTokenService;
import com.innovatrix.ahaar.service.UserService;
import com.innovatrix.ahaar.service.UserServiceInterface;
import com.innovatrix.ahaar.util.ResponseBuilder;
import jakarta.validation.Valid;
import org.hibernate.engine.jdbc.mutation.spi.BindingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/user")
public class UserController {

    UserServiceInterface userService;
    RefreshTokenService refreshTokenService;
    JWTService jwtService;

    @Autowired
    public UserController(UserService userService, RefreshTokenService refreshTokenService, JWTService jwtService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @GetMapping("/")
    public ResponseEntity<APIResponse<Page<ApplicationUser>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ApplicationUser> allUsers =  userService.getUsers(page, size);

        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "User retrieved successfully", allUsers));
    }

    @GetMapping(path = "{user_id}")
    public ResponseEntity<APIResponse<Optional<ApplicationUser>>> getUserById(@PathVariable("user_id") Long id) {
        Optional<ApplicationUser> user = userService.getUserById(id);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "User retrieved successfully", user));
    }

    @PutMapping(path = "{user_id}")
    public ResponseEntity<APIResponse<ApplicationUserDTO>> updateUser(@PathVariable("user_id") Long userId,
                           @Valid @RequestBody ApplicationUserDTO userDTO) {
        userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "User updated successfully", userDTO));
    }

    @DeleteMapping(path = "{user_id}")
    public ResponseEntity<APIResponse<ApplicationUserDTO>> deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "User deleted successfully", null));

    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<Optional<ApplicationUser>>> addUser(@Valid @RequestBody ApplicationUserDTO userDTO, BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        Optional<ApplicationUser> newUser = userService.addUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseBuilder.success(HttpStatus.CREATED.value(), "User created successfully", newUser));
    }

    /**
     +     * Authenticates user credentials and generates JWT token
     +     * @param loginDTO Contains user login credentials
     +     * @return JWT token string upon successful authentication
     +     * @throws AuthenticationException If credentials are invalid
     +     */
    @PostMapping("/login")
    public ResponseEntity<APIResponse<JwtResponseDTO>> authenticateAndGetToken(@Valid @RequestBody LoginDTO loginDTO, BindingResult bindingResult) {

        try {
            String jwtToken = userService.login(loginDTO);
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
    public ResponseEntity<APIResponse<JwtResponseDTO>> getRefreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getUserName());
                    JwtResponseDTO jwtResponseDTO = JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequest.getRefreshToken())
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "JWT token generated successfully", jwtResponseDTO));
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token", null)));
    }
}
