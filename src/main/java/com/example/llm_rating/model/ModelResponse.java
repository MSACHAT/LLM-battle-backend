package com.example.llm_rating.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelResponse {
    private String id;
    private String modelName;

    public ModelResponse(String id, String modelName) {
        this.id = id;
        this.modelName = modelName;
    }
// getters and setters
}