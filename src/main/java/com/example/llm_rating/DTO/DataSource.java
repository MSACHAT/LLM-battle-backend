package com.example.llm_rating.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataSource {
    private String dataSource;
    private String category;
    private String lastUpdated;

    @JsonProperty("arena_table")
    private List<ArenaTableItem> arenaTable;


}
