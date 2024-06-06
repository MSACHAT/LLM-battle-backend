package com.example.llm_rating.controller;

import com.example.llm_rating.model.UserEntity;
import com.example.llm_rating.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
}
