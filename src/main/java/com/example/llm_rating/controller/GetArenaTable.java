package com.example.llm_rating.controller;

import com.example.llm_rating.DTO.DataSource;
import com.example.llm_rating.service.CommunicationService;
import com.example.llm_rating.service.GetTableService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class GetArenaTable {

    private final CommunicationService communicationService;


    private final GetTableService getTableService;

    private final ObjectMapper objectMapper;
    @GetMapping("/arena_table")
    @Cacheable("arenaTableCache")
    public List<DataSource> getArenaTable() throws IOException {

        JsonNode eloTable= communicationService.getLeaderBoard();
        List<DataSource> sourceList = objectMapper.convertValue(eloTable, new TypeReference<List<DataSource>>() {});

        return sourceList.stream()
                .map(getTableService::processData)
                .toList();

    }
}
