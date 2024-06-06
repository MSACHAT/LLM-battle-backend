package com.example.llm_rating.model;

import java.util.List;

public class StreamData {
    private String conversationId;
    private StreamMessage message;
    private StreamMessageDetail messageDetail;

    public static class StreamMessage {
        private int index;
        private String messageId;
        private String title;
        private long lastMessageTime;
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
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getLastMessageTime() {
            return lastMessageTime;
        }

        public void setLastMessageTime(long lastMessageTime) {
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

    public static class StreamMessageDetail {
        private String content;
        private String contentType;
        private Message message;

        public static class Message {
            private String role;
            private long time;

            // Getters and setters
            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }
        }

        // Getters and setters
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }

    // Getters and setters for StreamData fields
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public StreamMessage getMessage() {
        return message;
    }

    public void setMessage(StreamMessage message) {
        this.message = message;
    }

    public StreamMessageDetail getMessageDetail() {
        return messageDetail;
    }

    public void setMessageDetail(StreamMessageDetail messageDetail) {
        this.messageDetail = messageDetail;
    }
}
