package com.example.llm_rating.controller;

import org.springframework.web.bind.annotation.*;

import com.example.llm_rating.dto.ReturnDto;
import com.example.llm_rating.dto.VoteDto;
import com.example.llm_rating.service.impl.GetLeaderboard;
import com.example.llm_rating.service.impl.VoteServiceImpl;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @PostMapping("/sidebyside_anonymous_log")
    public ReturnDto sidebysideAnoymousLog(@RequestBody VoteDto dto) {
        try {
            voteService.addVoteData(dto);
            return new ReturnDto(true, "success", null);
        } catch (Exception e) {
            return new ReturnDto(false, e.getMessage(), null);
        }

    }

    @PostMapping("/sidebyside_anonymous")
    public ReturnDto sidebysideAnoymous(@RequestBody VoteDto dto) {
        try {

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDateTime = now.format(formatter);
            String fileName = formattedDateTime+"-conv.json";
            voteService.appendToJsonFile(fileName,dto);
            return new ReturnDto(true,"success",null);
        } catch (Exception e) {
            return new ReturnDto(false,e.getMessage(),null);
        }

    }

    @GetMapping("/leaderboard")
    public ReturnDto leaderboard() {
        try {
           Object data = getLeaderboard.getLeaderBoard();
            return new ReturnDto(true, "success", data);
        } catch (Exception e) {
            return new ReturnDto(false, e.getMessage(), null);
        }

    }


}
