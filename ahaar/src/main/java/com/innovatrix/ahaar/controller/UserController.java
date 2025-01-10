package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.ApplicationUserDTO;
import com.innovatrix.ahaar.model.LoginDTO;
import com.innovatrix.ahaar.service.UserService;
import com.innovatrix.ahaar.service.UserServiceInterface;
import com.innovatrix.ahaar.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/user")
public class UserController {

    UserServiceInterface userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
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

    @PostMapping("/signup")
    public ResponseEntity<APIResponse<Optional<ApplicationUser>>> addUser(@RequestBody ApplicationUserDTO userDTO) {
        Optional<ApplicationUser> newUser = userService.addUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseBuilder.success(HttpStatus.OK.value(), "User created successfully", newUser));
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO) {
        System.out.println(loginDTO);
        return userService.login(loginDTO);
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
}
