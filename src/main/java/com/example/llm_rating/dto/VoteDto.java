package com.example.llm_rating.dto;

import java.util.Date;
import java.util.List;

import com.example.llm_rating.entity.VoteEntity.States;

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
    private String conv_id;
    private List<String> models;
    private String winner;
    private boolean anony;
    private List<States> states;
    private String ip;

    public static class States {
        private String model_name;
        private List<Message> messages;
        private int offset;
        private String conv_id;
    }

    public static class Message {
        private String role;
        private String content;
    }
}
