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
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
public class LoginController {


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @RequestMapping("/loginSuccess")
    public String loginSuccess() {

        return "redirect:/";
    }

    @RequestMapping("/logoutSuccess")
    public String logoutSuccess() {
        return "redirect:/login?logout";
    }
}
