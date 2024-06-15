package com.example.llm_rating.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "model")
public class Model {
    @Id
    private String id;
    @Field("bot_id")
    private String botId;

    @Field("knowledge_cutoff")
    private String knowledgeCutoff;

    private String lisence;

    private String organization;

    public String getKnowledgeCutoff() {
        return knowledgeCutoff;
    }

    public void setKnowledgeCutoff(String knowledgeCutoff) {
        this.knowledgeCutoff = knowledgeCutoff;
    }

    public String getLisence() {
        return lisence;
    }

    public void setLisence(String lisence) {
        this.lisence = lisence;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    @Field("model_name")
    private String modelName;
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String url;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
// Getters and Setters
    // ...
}
