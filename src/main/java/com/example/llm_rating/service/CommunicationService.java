package com.example.llm_rating.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CommunicationService {


    private final OkHttpClient okHttpClient;

    @Value("${api.leaderboard-url}")
    private String getLeaderBoardUrl;

    @Value("${api.computeELO-url}")
    private String computeEloUrl;

    public JsonNode getLeaderBoard() throws IOException {
        Request request = new Request.Builder()
                .url(getLeaderBoardUrl)
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
                .url(computeEloUrl)
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
