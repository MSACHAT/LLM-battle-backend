package com.example.llm_rating.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "votes")
public class VoteEntity {
    @Id
    private String id;
    private String type;
    private Date tstamp;

    @Field("conv_id")
    private String convId;
    private List<String> models;
    private String winner;
    private boolean anony;
    private List<States> states;
    private String ip;

    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class States {

        @Field("model_name")
        private String modelName;
        private List<Message> messages;
        private int offset;

        @Field("conv_id")
        private String convId;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
