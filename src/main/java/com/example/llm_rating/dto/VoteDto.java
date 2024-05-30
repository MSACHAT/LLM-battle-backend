package com.example.llm_rating.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoteDto {
    private String type;
    private Date tstamp;
    private String convId;
    private List<String> models;
    private String winner;
    private boolean anony;
    private List<States> states;
    private String ip;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class States {
        private String modelName;
        private List<Message> messages;
        private int offset;
        private String convId;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
