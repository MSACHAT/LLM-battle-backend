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
    private String Rank;
    private String Model;
    private int Elo;
    private String CI;
    private int Votes;
    private String knowledgeCutoff;
    private String organization;
    private String license;


}

