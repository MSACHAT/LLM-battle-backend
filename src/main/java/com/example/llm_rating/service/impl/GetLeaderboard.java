package com.example.llm_rating.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetLeaderboard {


    private final OkHttpClient okHttpClient;

    public JsonNode getLeaderBoard() throws IOException {
        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/api/v1/leaderboard")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            assert response.body() != null;
            String responseData = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(responseData);
        }
    }

    public String computeElo() throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"key\":\"value\"}";
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url("http://localhost:8000/api/v1/compute_elo")
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            assert response.body() != null;
            return response.body().string();
        }
    }



}
