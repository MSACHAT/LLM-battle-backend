package com.example.llm_rating.model;

import org.bson.BsonTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "message_detail")
public class MessageDetail {
    @Id
    private String id;
    private String content;

    @Field("content_type")
    private String contentType;

    private Media media;
    private String role;
    private BsonTimestamp time;

    public MessageDetail(String content, String contentType, String role) {
        this.content = content;
        this.contentType = contentType;
        this.role = role;
    }

    // Getters and setters for MessageDetail fields
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BsonTimestamp getTime() {
        return time;
    }

    public void setTime(BsonTimestamp time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Media getMedia() {
        return media;
    }

    public void setMessage(Media media) {
        this.media = media;
    }

    private static class Media {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
