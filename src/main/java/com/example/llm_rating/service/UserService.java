package com.example.llm_rating.service;

import com.example.llm_rating.model.UserEntity;
import com.example.llm_rating.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public String login(UserEntity user) {
        UserEntity userExists = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(() -> new Error("Username password not match"));
        return jwtService.setToken(userExists);
    }

    public void register(UserEntity user) {
        userRepository.insert(new UserEntity(user.getUsername(), user.getPassword()));
    }
}
