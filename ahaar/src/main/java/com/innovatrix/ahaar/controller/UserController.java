package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.exception.NoDataFoundException;
import com.innovatrix.ahaar.model.APIResponse;
import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.ApplicationUserDTO;
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

    @GetMapping("/all")
    public ResponseEntity<APIResponse<Page<ApplicationUser>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ApplicationUser> allUsers =  userService.getUsers(page, size);

        if(allUsers.isEmpty()) {
            throw new NoDataFoundException("No user to retrieve");
        }

        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "User retrieved successfully", allUsers));
    }

    @GetMapping(path = "/{user_id}")
    public ResponseEntity<APIResponse<Optional<ApplicationUser>>> getUserById(@PathVariable("user_id") Long id) {
        Optional<ApplicationUser> user = userService.getUserById(id);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(), "User retrieved successfully", user));
    }


    @PostMapping("/")
    public ResponseEntity<APIResponse<Optional<ApplicationUser>>> addUser(@RequestBody ApplicationUserDTO userDTO) {
        Optional<ApplicationUser> newUser = userService.addUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseBuilder.success(HttpStatus.OK.value(), "User created successfully", newUser));
    }

    @PutMapping(path = "/{user_id}")
    public ResponseEntity<APIResponse<ApplicationUserDTO>> updateUser(@PathVariable("user_id") Long userId,
                           @RequestBody ApplicationUserDTO userDTO) {
        userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(),"User updated successfully", userDTO));
    }

    @DeleteMapping(path = "/{user_id}")
    public ResponseEntity<APIResponse<ApplicationUserDTO>> deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ResponseBuilder.success(HttpStatus.OK.value(),"User deleted successfully", null));

    }
}
