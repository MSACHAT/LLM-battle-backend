package com.example.llm_rating.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.MediaType;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.*;

@Controller
@RequestMapping("/api")
public class WebSocketController {
    @Value("${target.api.url}")
    private String targetUrl;

    @Value("${api.token}")
    private String apiToken;



    /**
     * 处理将数据转发到目标 API 的 POST 请求。
     *
     * @return Flux<String> 从目标 API 接收到的响应。
     */
    @PostMapping(value = "/forward",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> forwardRequest() {

        System.out.println(targetUrl);
        System.out.println(1111);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.set("Content-Type", "application/json");
        // 设置请求体
        String requestBody = "{ \"chat_history\": [{ \"role\": \"user\", \"content\": \"输出333\" }, { \"role\": \"user\", \"content\": \"输出nnn\" }], \"bot_id\": \"7369475330840576001\", \"user\": \"7345091449232851969\", \"query\": \"111\",\"stream\": true}";

        WebClient webClient = WebClient.create();
        // 发送 POST 请求，并返回响应的Flux
        Flux<String> res = webClient.post()
                .uri(targetUrl)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class);

        return res;
    }
}
