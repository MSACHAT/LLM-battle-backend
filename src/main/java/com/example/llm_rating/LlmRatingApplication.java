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
	String json = "{\"event\": \"message\", \"message\": {\"role\": \"assistant\", \"type\": \"answer\", \"content\": \"222\", \"content_type\": \"text\"}, \"is_finish\": false, \"index\": 0, \"conversation_id\": \"c2714238667a4aeab546dfd9ddfe77e9\", \"seq_id\": 0}";

	ObjectMapper objectMapper = new ObjectMapper();

}

