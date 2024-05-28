package com.example.llm_rating.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.llm_rating.entity.VoteEntity;

public interface VoteRepository extends MongoRepository<VoteEntity, String> {

}
