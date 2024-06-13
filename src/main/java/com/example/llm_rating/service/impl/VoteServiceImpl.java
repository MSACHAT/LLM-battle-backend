package com.example.llm_rating.service.impl;

import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.example.llm_rating.dto.VoteDto;
import com.example.llm_rating.entity.VoteEntity;
import com.example.llm_rating.repository.VoteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Service
@AllArgsConstructor
public class VoteServiceImpl {

    private static final Logger logger = LogManager.getLogger(VoteServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private VoteRepository voteRepository;

    private ModelMapper modelMapper;

    public Object insertVote(VoteDto data) {
        VoteEntity voteEntity = modelMapper.map(data, VoteEntity.class);

        Object votes = voteRepository.save(voteEntity);
        return votes;
    }

    public void addVoteData(VoteDto voteDto) {

        try {

            String jsonString = objectMapper.writeValueAsString(voteDto);

            logger.info(jsonString);
        } catch (Exception e) {
            logger.error("Error serializing object", e);
        }
    }

    // public void addVoteDataTo(VoteDto voteDto) {
    // try {
    // return null;
    // } catch (JsonProcessingException e) {
    // throw new RuntimeException(e);
    // }
    // }
    public void appendToJsonFile(String filePath, Object newContentJson) {
        try {

            File file = new File(filePath);

            String newContentJsonString = objectMapper.writeValueAsString(newContentJson);

            ArrayNode jsonArray;

            if (file.exists()) {

                JsonNode existingJson = objectMapper.readTree(file);

                if (existingJson.isArray()) {
                    jsonArray = (ArrayNode) existingJson;
                } else {
                    jsonArray = objectMapper.createArrayNode();
                    jsonArray.add(existingJson);
                }
            } else {

                file.createNewFile();
                jsonArray = objectMapper.createArrayNode();
            }

            JsonNode newContentNode = objectMapper.readTree(newContentJsonString);
            jsonArray.add(newContentNode);

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, jsonArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
