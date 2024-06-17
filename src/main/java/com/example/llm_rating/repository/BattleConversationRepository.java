package com.example.llm_rating.repository;

import com.example.llm_rating.model.BattleConversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BattleConversationRepository extends MongoRepository<BattleConversation, String> {

    Optional<BattleConversation> findByConversationId(String conversationId);

    List<BattleConversation> findByUserId(String userId);

    List<BattleConversation> findByBattleId(String battleId);
}