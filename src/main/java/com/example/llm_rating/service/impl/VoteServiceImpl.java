package com.example.llm_rating.service.impl;

import com.example.llm_rating.dto.VoteDto;
import com.example.llm_rating.entity.VoteEntity;
import com.example.llm_rating.repository.VoteRepository;

import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VoteServiceImpl {

    private VoteRepository voteRepository;

    private ModelMapper modelMapper;

    public Object insertVote(VoteDto data) {
        VoteEntity voteEntity = modelMapper.map(data, VoteEntity.class);
        Object votes = voteRepository.save(voteEntity);
        return votes;
    }
}
