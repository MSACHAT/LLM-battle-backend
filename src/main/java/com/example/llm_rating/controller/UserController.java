package com.example.llm_rating.controller;

import com.example.llm_rating.model.UserEntity;
import com.example.llm_rating.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @PostMapping("/login")
    public String login(@RequestBody UserEntity user) {
        return userService.login(user);
    }

    @PostMapping("/register")
    public void register(@RequestBody UserEntity user) {
        userService.register(user);
    }

    @GetMapping("/test")
    public String test(Authentication auth){
        return auth != null ? auth.getName(): null;
    }
}
