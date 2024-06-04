package com.example.llm_rating.service.impl;

import okhttp3.*;

import java.io.IOException;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetLeaderboard {
    private final OkHttpClient okHttpClient;

    public String getExample() throws IOException {
        Request request = new Request.Builder()
                .url("https://api.example.com/data")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        }
    }

    public String postExample() throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"key\":\"value\"}";
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url("https://api.example.com/data")
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        }
    }

}
