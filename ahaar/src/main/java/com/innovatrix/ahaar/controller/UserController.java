package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.DTO.JwtResponseDTO;
import com.innovatrix.ahaar.DTO.RefreshTokenRequestDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/all")
    public ResponseEntity<APIResponse<Page<ApplicationUser>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ApplicationUser> allUsers =  userService.getUsers(page, size);

        if(allUsers.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), "No user to retrieve", allUsers));
        }

        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "User retrieved successfully", allUsers));
    }

    @GetMapping(path = "{user_id}")
    public ResponseEntity<APIResponse<Optional<ApplicationUser>>> getUserById(@PathVariable("user_id") Long id) {
        Optional<ApplicationUser> user = userService.getUserById(id);

        if(user.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), "User not found", null));
        }

        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "User retrieved successfully", user));
    }

    @PutMapping(path = "{user_id}")
    public ResponseEntity<APIResponse<ApplicationUser>> updateUser(@PathVariable("user_id") Long userId,
                           @RequestBody ApplicationUserDTO userDTO) {

        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(),"User updated successfully", userService.updateUser(userId, userDTO)));
    }

    @DeleteMapping(path = "{user_id}")
    public ResponseEntity<APIResponse<ApplicationUserDTO>> deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(),"User deleted successfully", null));

    }

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<Optional<ApplicationUser>>> addUser(@RequestBody ApplicationUserDTO userDTO) {
        Optional<ApplicationUser> newUser = userService.addUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseBuilder.success(HttpStatus.OK.value(), "User created successfully", newUser));
    }
    
    /**
     +     * Authenticates user credentials and generates JWT token
     +     * @param loginDTO Contains user login credentials
     +     * @return JWT token string upon successful authentication
     +     * @throws AuthenticationException If credentials are invalid
     +     */
    @PostMapping("/login")
    public ResponseEntity<APIResponse<JwtResponseDTO>> authenticateAndGetToken(@RequestBody LoginDTO loginDTO) {
        String jwtToken = userService.login(loginDTO);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginDTO.getUsername());
        JwtResponseDTO jwtResponseDTO = JwtResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken()).build();

        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "logged in successfully", jwtResponseDTO))    ;
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<APIResponse<JwtResponseDTO>> getRefreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.toDTO().getUserName());
                    JwtResponseDTO jwtResponseDTO = JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequest.getRefreshToken())
                            .build();
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success(HttpStatus.OK.value(), "JWT token generated successfully", jwtResponseDTO));
                }).orElseThrow(() -> new RuntimeException(
                        "Refresh token is not in database!"));
    }
}
