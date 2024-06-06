package com.example.llm_rating.model;

public class ConversationRequest {
    private String conversationId;
    private int count;

    // Getter and setter methods
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
