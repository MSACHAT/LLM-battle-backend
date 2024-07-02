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
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.*;

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

    private final RetryTemplate retryTemplate;

    @GetMapping("/arena_table")
    @Cacheable("arenaTableCache")
    public List<DataSource> getArenaTable() throws IOException {

        Object eloTable= communicationService.getLeaderBoard();
        List<DataSource> sourceList = objectMapper.convertValue(eloTable, new TypeReference<List<DataSource>>() {});

        return sourceList.stream()
                .map(getTableService::processData)
                .toList();

    }

    @PostMapping("/is_success/{isSuccess}")
    public Boolean isComputeSuccess(@PathVariable String isSuccess) throws IOException {
        if ("false".equalsIgnoreCase(isSuccess)) {
            retryTemplate.execute(new RetryCallback<Void, RuntimeException>() {
                @Override
                public Void doWithRetry(RetryContext context) {
                    try {
                        communicationService.computeElo();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
            });
        } else {
            communicationService.computeElo();
        }
        return true;
    }

    @GetMapping("/arena_table/test")
    public Object testGetArenaTable() throws IOException {
        Object eloTable= communicationService.getLeaderBoard();
        List<DataSource> sourceList = objectMapper.convertValue(eloTable, new TypeReference<List<DataSource>>() {});

        return sourceList;
    }
}
