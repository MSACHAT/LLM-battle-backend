package com.example.llm_rating.service;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;


import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VoteService {
    private ModelMapper modelMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
