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

    private String licence;

    private String organization;
    @Field("model_name")
    private String modelName;
    private String token;
    private String url;


}
