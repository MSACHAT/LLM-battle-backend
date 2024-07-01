package com.example.llm_rating;

import com.example.llm_rating.model.StreamData;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.core.model.Model;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
@EnableRetry
public class LlmRatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlmRatingApplication.class, args);
	}



	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

}
