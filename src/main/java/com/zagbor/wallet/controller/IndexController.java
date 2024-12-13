package com.zagbor.wallet.controller;

import com.zagbor.wallet.model.User;
import com.zagbor.wallet.model.Wallet;
import com.zagbor.wallet.service.UserService;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class IndexController {

    private final UserService userService;

    @GetMapping("/")
    public String categories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean exists = userService.checkUserExists(authentication.getName());
        if (!exists) {
            userService.createUser(new User(authentication.getName(), new Wallet(new ArrayList<>())));
        }
        return "index";
    }
}