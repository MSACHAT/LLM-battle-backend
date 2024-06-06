package cn.moonshotacademy.arena.service;

import org.springframework.stereotype.Service;

import cn.moonshotacademy.arena.entity.UserEntity;
import cn.moonshotacademy.arena.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
        System.out.println(user.getUsername() + user.getPassword());
        userRepository.insert(new UserEntity(user.getUsername(), user.getPassword()));
    }
}
