package com.example.llm_rating.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String password;
    private int uid;
    private String username;

    // Getters and Setters
    // ...
}
