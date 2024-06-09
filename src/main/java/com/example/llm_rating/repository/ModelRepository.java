package com.example.llm_rating.repository;

import com.example.llm_rating.model.Model;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ModelRepository extends MongoRepository<Model, String> {
    Optional<Model> findByModelName(String modelName);
}