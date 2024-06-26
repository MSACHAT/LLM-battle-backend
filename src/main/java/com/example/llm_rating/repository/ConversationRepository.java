package com.example.llm_rating.repository;

import com.example.llm_rating.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends MongoRepository<Conversation, String> {

    Optional<Conversation> findByConversationId(String conversationId);

    List<Conversation> findByUserId(String userId);
}