package com.example.llm_rating.service;

import com.example.llm_rating.model.MessageDetail;
import com.example.llm_rating.model.MessageResponse;
import com.example.llm_rating.repository.MessageDetailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;


import java.time.Duration;
import java.util.List;
import java.util.Map;


@Service
public class ChatService {
    @Value("${target.api.url}")
    private String targetUrl;

    @Value("${api.token}")
    private String apiToken;

    @Autowired
    private MessageDetailRepository messageDetailRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private String fulltext = "";
    private boolean alive = true;

    public void stopped() {
        System.out.println(alive);
        alive = false;
        System.out.println(alive);
    }


    public Flux<String> getStreamAnswer1(String contentType,  String query1, List<MessageResponse> history1) {


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.set("Content-Type", "application/json");
        // 设置请求体
        System.out.println();
        alive = true;

        String query = query1;

        List<MessageResponse> history = history1;

        StringBuilder requestBodyBuilder = new StringBuilder();
        requestBodyBuilder.append("[ ");

        for (int i = 0; i < history.size(); i++) {
            MessageResponse message = history.get(i);
            String role = message.getRole();
            String content = message.getContent();

            requestBodyBuilder.append("{ \"role\": \"").append(role).append("\", \"content\": \"").append(content).append("\" }");

            if (i != history.size() - 1) {
                requestBodyBuilder.append(", ");
            }
        }

        requestBodyBuilder.append(" ]");

        String requestBody = "{ " +
                "\"chat_history\": " + requestBodyBuilder.toString() + ", " +
                "\"bot_id\": \"7372102895011463176\", " +
                "\"user\": \"1481156807020\", " +
                "\"query\": \"" + query + "\", " +
                "\"stream\": true" +
                "}";




        WebClient webClient = WebClient.create();
        // 发送 POST 请求，并返回响应的Flux
        Flux<String> res = webClient.post()
                .uri(targetUrl)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .takeWhile(data -> alive);



        fulltext = "";

        res
                .doOnComplete(() -> {
                    MessageDetail messageDetail = new MessageDetail(
                            fulltext,
                            "text",
                            "assistant");
                    MessageDetail messageMongo = messageDetailRepository.save(messageDetail);
                    System.out.println(messageMongo.getId());
                })
                .subscribe(event ->{
                    ObjectMapper mapper = new ObjectMapper();
                    try{
                        JsonNode message = mapper.readTree(event);
                        if (message.get("message").get("type").asText().equals("verbose") ){ this.alive = false;}
                        if (message.has("message")&&message.get("message").get("type").asText().equals("answer")){
                            fulltext += message.get("message").get("content").asText();

//                    System.out.print(message.get("message").get("content").asText());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                });

        return res;

    }





    public Flux<String> getStreamAnswer() {
                HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.set("Content-Type", "application/json");
        // 设置请求体
        String requestBody = "{ \"chat_history\": [{ \"role\": \"user\", \"content\": \"输出333\" }, { \"role\": \"user\", \"content\": \"输出nnn\" }], \"bot_id\": \"7372102895011463176\", \"user\": \"1481156807020\", \"query\": \"写一篇50字的文章\",\"stream\": true}";

        WebClient webClient = WebClient.create();
        // 发送 POST 请求，并返回响应的Flux
        Flux<String> res = webClient.post()
                .uri(targetUrl)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .takeWhile(data ->true);

        fulltext = "";

        res
                .doOnComplete(() -> {
                    MessageDetail messageDetail = new MessageDetail(
                            fulltext,
                            "text",
                            "assistant");
                    MessageDetail messageMongo = messageDetailRepository.save(messageDetail);
                    System.out.println(messageMongo.getId());
                })
                .subscribe(event ->{
            ObjectMapper mapper = new ObjectMapper();
            try{
                JsonNode message = mapper.readTree(event);
                if (message.has("message")&&message.get("message").get("type").asText().equals("answer")){
                    fulltext += message.get("message").get("content").asText();

//                    System.out.print(message.get("message").get("content").asText());
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        });

        return res;

    }







//        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
//
//
//
//
//        new Thread(() -> {
//            try {
//                // Simulating delay between streaming events
//                Thread.sleep(1000);
//                sink.tryEmitNext(getMockResponse("222", 0, false));
//
//                Thread.sleep(1000);
//                sink.tryEmitNext(getMockResponse("333", 1, false));
//
//                Thread.sleep(1000);
//                sink.tryEmitNext(getMockResponse("nnn", 2, true));
//
//                sink.tryEmitComplete();
//            } catch (InterruptedException e) {
//                sink.tryEmitError(e);
//            }
//        }).start();
//
//        return sink.asFlux().delayElements(Duration.ofMillis(1000));




    private String getMockResponse(String content, int seqId, boolean isFinish) {
        return String.format("{\"event\": \"message\", \"message\": {\"role\": \"assistant\", \"type\": \"answer\", \"content\": \"%s\", \"content_type\": \"text\"}, \"is_finish\": %b, \"index\": 0, \"conversation_id\": \"c2714238667a4aeab546dfd9ddfe77e9\", \"seq_id\": %d}", content, isFinish, seqId);
    }
}
