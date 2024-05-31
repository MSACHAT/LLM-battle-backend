package com.example.llm_rating.service.impl;

import com.example.llm_rating.dto.VoteDto;
import com.example.llm_rating.entity.VoteEntity;
import com.example.llm_rating.repository.VoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

}
