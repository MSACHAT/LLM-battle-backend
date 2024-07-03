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
    Request request = new Request.Builder()
            .url(computeEloUrl) // 假设computeEloUrl已经定义并指向正确的URL
            .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "")) // 使用空字符串作为请求体
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
