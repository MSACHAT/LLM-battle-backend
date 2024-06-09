package com.example.llm_rating.model.DTO;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@Document(collection = "messages")
    public class MessageSaveDto {
        @Id
        private String id;
        private int index;
        private String messageId;

        // Constructors, Getters, and Setters
    }

