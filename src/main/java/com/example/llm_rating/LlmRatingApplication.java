package com.example.llm_rating;

import com.example.llm_rating.model.StreamData;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.core.model.Model;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LlmRatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlmRatingApplication.class, args);
	}

	ObjectMapper objectMapper = new ObjectMapper();

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
