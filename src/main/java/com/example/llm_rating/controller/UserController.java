package com.example.llm_rating.controller;

import com.example.llm_rating.model.UserEntity;
import com.example.llm_rating.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


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
    public ResponseEntity<HashMap<String, Object>> register(@RequestBody UserEntity user) {
        userService.register(user);

        HashMap<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "请求成功");

        // 如果您有额外的数据要添加到响应体，可以直接添加到 HashMap 中
        // response.put("data", yourDataObject);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String test(Authentication auth) {
        return auth != null ? auth.getName() : null;
    }
}
