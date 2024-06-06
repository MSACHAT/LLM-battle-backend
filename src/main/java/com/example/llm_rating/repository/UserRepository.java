package com.example.llm_rating.repository;

import java.util.Optional;

import com.example.llm_rating.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;




public interface UserRepository extends MongoRepository<UserEntity, String> {
    
    Optional<UserEntity> findByUsernameAndPassword(String username, String password);

    Boolean existsByUsername(String username);
}
