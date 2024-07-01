package com.example.llm_rating.repository;

import com.example.llm_rating.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByUsernameAndPassword(String username, String password);

    Boolean existsByUsername(String username);
}
