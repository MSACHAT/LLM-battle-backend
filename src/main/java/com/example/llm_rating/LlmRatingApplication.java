package com.example.llm_rating;

import com.example.llm_rating.model.StreamData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LlmRatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlmRatingApplication.class, args);
	}

	ObjectMapper objectMapper = new ObjectMapper();

}

