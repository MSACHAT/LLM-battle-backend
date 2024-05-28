package com.example.llm_rating.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.llm_rating.dto.ReturnDto;
import com.example.llm_rating.dto.VoteDto;
import com.example.llm_rating.service.impl.VoteServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/vote")
public class VoteController {

    private VoteServiceImpl voteService;

    @PostMapping("/sidebyside_anonymous")
    public ReturnDto sidebysideAnoymous(@RequestBody VoteDto entity) {
        try {
            Object data = voteService.insertVote(entity);
            return new ReturnDto(true, "success", data);
        } catch (Exception e) {
            return new ReturnDto(false, e.getMessage(), null);
        }

    }

}
