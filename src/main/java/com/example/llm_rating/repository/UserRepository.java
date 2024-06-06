package cn.moonshotacademy.arena.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.moonshotacademy.arena.entity.UserEntity;


public interface UserRepository extends MongoRepository<UserEntity, String> {
    
    Optional<UserEntity> findByUsernameAndPassword(String username, String password);

    Boolean existsByUsername(String username);
}
