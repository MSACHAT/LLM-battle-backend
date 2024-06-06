package com.example.llm_rating.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.llm_rating.dto.ReturnDto;
import com.example.llm_rating.dto.VoteDto;
import com.example.llm_rating.service.impl.GetLeaderboard;
import com.example.llm_rating.service.impl.VoteServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/vote")
public class VoteController {

    private VoteServiceImpl voteService;
    private GetLeaderboard getLeaderboard;

    @PostMapping("/sidebyside_anonymous_db")
    public ReturnDto sidebysideAnoymousDb(@RequestBody VoteDto entity) {
        try {
            Object data = voteService.insertVote(entity);
            return new ReturnDto(true, "success", data);
        } catch (Exception e) {
            return new ReturnDto(false, e.getMessage(), null);
        }

    }

    @PostMapping("/sidebyside_anonymous")
    public ReturnDto sidebysideAnoymous(@RequestBody VoteDto dto) {
        try {
            voteService.addVoteData(dto);
            return new ReturnDto(true, "success", null);
        } catch (Exception e) {
            return new ReturnDto(false, e.getMessage(), null);
        }

    }

    @PostMapping("/leaderboard")
    public ReturnDto leaderboard() {
        try {
            getLeaderboard.getLeaderboard();
            return new ReturnDto(true, "success", null);
        } catch (Exception e) {
            return new ReturnDto(false, e.getMessage(), null);
        }

    }

}
