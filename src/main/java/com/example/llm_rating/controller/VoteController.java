package com.example.llm_rating.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.llm_rating.dto.VoteDto;

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

    @PostMapping("/sidebyside_anonymous")
    public String sidebysideAnoymous(@RequestBody VoteDto entity) {

        return entity;
    }

}
