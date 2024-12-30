package com.innovatrix.ahaar.controller;

import com.innovatrix.ahaar.model.MyUser;
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
    public List<MyUser> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(path = "{user_id}")
    public Optional<MyUser> getUserById(@PathVariable("user_id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public void addUser(@RequestBody MyUser user) {
        userService.addUser(user);
    }

    @PutMapping(path = "{user_id}")
    public void updateUser(@PathVariable("user_id") Long userId,
                           @RequestBody MyUser user) {
        userService.updateUser(userId, user);
    }

    @DeleteMapping(path = "{user_id}")
    public void deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);
    }
}
