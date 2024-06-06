package com.example.llm_rating.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "model")
public class Model {
    @Id
    private String id;
    private String modelName;
    private String token;
    private String type;
    private String url;

    // Getters and Setters
    // ...
}
