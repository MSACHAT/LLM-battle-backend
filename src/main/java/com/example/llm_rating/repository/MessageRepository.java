package com.example.llm_rating.repository;

import com.example.llm_rating.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Conversation.Message, String> {

}