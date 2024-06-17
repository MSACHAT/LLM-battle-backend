package com.example.llm_rating.repository;

import com.example.llm_rating.model.MessageDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MessageDetailRepository extends MongoRepository<MessageDetail, String> {
    Optional<MessageDetail> findById(ObjectId messageId);

}