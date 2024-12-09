package com.zagbor.wallet.controller;

import com.zagbor.wallet.manager.UserManager;
import com.zagbor.wallet.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManager userManager;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userManager.createUser(user);
    }

    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) {
        return userManager.getUserByUsername(username).orElse(null);  // или выбрасываем исключение, если не найдено
    }

    @PutMapping("/{username}")
    public boolean updateUser(@PathVariable String username, @RequestBody User user) {
        return userManager.updateUser(username, user);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username) {
        userManager.removeUser(username);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userManager.getAllUsers();
    }
}