package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.model.ApplicationUser;
import com.innovatrix.ahaar.model.ApplicationUserDTO;
import com.innovatrix.ahaar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping()
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<ApplicationUser> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(path = "{user_id}")
    public Optional<ApplicationUser> getUserById(@PathVariable("user_id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public void addUser(@RequestBody ApplicationUserDTO userDTO) {
        userService.addUser(userDTO);
    }

    @PutMapping(path = "{user_id}")
    public void updateUser(@PathVariable("user_id") Long userId,
                           @RequestBody ApplicationUserDTO userDTO) {
        userService.updateUser(userId, userDTO);
    }

    @DeleteMapping(path = "{user_id}")
    public void deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);
    }
}
