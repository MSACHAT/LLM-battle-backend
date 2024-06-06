package com.example.llm_rating.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "conversation")
public class Conversation {
    @Id
    private String id;

    @Field("conversation_id")
    private String conversationId;
    private String modelId;
    private List<Message> messages = new ArrayList<>();

    public void setModelId(String modelId) {
    }

    public static class Message {
        private int index;
        private String message_id;
        private String title;
        private Timestamp lastMessageTime;
        private String modelId;
        private String userId;

        // Getters and setters

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getMessageId() {
            return message_id;
        }

        public void setMessageId(String messageId) {
            this.message_id = messageId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Timestamp getLastMessageTime() {
            return lastMessageTime;
        }

        public void setLastMessageTime(Timestamp lastMessageTime) {
            this.lastMessageTime = lastMessageTime;
        }

        public String getModelId() {
            return modelId;
        }

        public void setModelId(String modelId) {
            this.modelId = modelId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }


    }

    // Getters and setters for Conversation fields
    public Conversation() {
        this.id = UUID.randomUUID().toString(); // Generate a unique id
    }

    public Conversation(String conversationId) {
         // Generate a unique id
        this.conversationId = conversationId;
    }
    public String getId() {
        return id;
    }

    public String getModelId() {
        return modelId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
