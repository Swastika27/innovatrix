package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.ApplicationUserDTO;
import com.innovatrix.ahaar.service.UserService;
import com.innovatrix.ahaar.service.UserServiceInterface;
import com.innovatrix.ahaar.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping()
public class UserController {

    UserServiceInterface userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<ApplicationUser>>> getUsers() {
        List<ApplicationUser> allUsers =  userService.getUsers();
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

    @PostMapping
    public ResponseEntity<APIResponse<Optional<ApplicationUser>>> addUser(@RequestBody ApplicationUserDTO userDTO) {
        Optional<ApplicationUser> newUser = userService.addUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseBuilder.success(HttpStatus.OK.value(), "User created successfully", newUser));
    }

    @PutMapping(path = "{user_id}")
    public ResponseEntity<APIResponse<ApplicationUserDTO>> updateUser(@PathVariable("user_id") Long userId,
                           @RequestBody ApplicationUserDTO userDTO) {
        userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(),"User updated successfully", userDTO));
    }

    @DeleteMapping(path = "{user_id}")
    public ResponseEntity<APIResponse<ApplicationUserDTO>> deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(),"User deleted successfully", null));

    }
}
