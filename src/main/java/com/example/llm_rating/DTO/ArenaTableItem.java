package com.example.llm_rating.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArenaTableItem {
    private String rank;
    private String model;
    private int elo;
    private String ci95;
    private int votes;
    private String knowledgeCutoff;
    private String organization;
    private String license;


}

