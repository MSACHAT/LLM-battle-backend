package com.example.llm_rating.controller;

import com.example.llm_rating.model.Conversation;
import com.example.llm_rating.model.MessageResponse;
import com.example.llm_rating.model.StreamData;
import com.example.llm_rating.service.ChatService;
import com.example.llm_rating.service.ConversationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Controller
@RequestMapping("/api")
public class ChatController {

    @Value("${target.api.url}")
    private String targetUrl;

    @Value("${api.token}")
    private String apiToken;

    private final ChatService chatService;
    private final ConversationService conversationService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    public ChatController(ChatService chatService, ConversationService conversationService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.conversationService = conversationService;
        this.objectMapper = objectMapper;
    }


    @GetMapping(value = "/conversation/get_message_list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MessageResponse>> getMessageList(@RequestBody Map<String, String> requestBody) {

        String conversationId = requestBody.get("conversationId");
        System.out.println(conversationId);
        if (conversationId == null || conversationId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<MessageResponse> allMessageResponses = conversationService.buildMessageResponses(conversationId);

        if (allMessageResponses == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(allMessageResponses);
    }

    @GetMapping(value = "/test")
    public ResponseEntity<HttpStatus> createConversation1() {
        Optional<Conversation> c = conversationService.getConversation();
        System.out.println(c.get().getMessages().get(0).getIndex());
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/conversation/break_message")
    public ResponseEntity<HttpStatus>breakMessage() {
        Optional<Conversation> c = conversationService.getConversation();
        System.out.println(c.get().getMessages().get(0).getIndex());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping (value = "/conversation/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String>newchat(@RequestBody Map<String, String> requestBody) {
        String contentType = requestBody.get("content_type");
        String conversationId = requestBody.get("convsersationId");
//        Map<String, Object> extra = request.getExtra();
         String query = requestBody.get("query");

        if (conversationId == null || conversationId.isEmpty()) {
            System.out.println("conversationId要后端新建吗");
        }

        List<MessageResponse> history = conversationService.buildMessageResponses(conversationId);


        System.out.println(history);

        // 如果返回结果为 null 或未找到，则返回空 Flux
        if (history == null) {
            return Flux.empty();
        }
        return chatService.getStreamAnswer1(contentType, query, history);

    }

    @PostMapping(value = "/conversation/create_conversation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createConversation(@RequestBody Map<String, String> requestBody) {
        String modelId = requestBody.get("model_id");
        if (modelId == null || modelId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Generate a unique conversation ID
        String conversationId = UUID.randomUUID().toString();
        // Generate a unique msToken
        String msToken = UUID.randomUUID().toString();

        // Create a new conversation and save it to the database
        Conversation newConversation = new Conversation();
        newConversation.setModelId(modelId);
        conversationService.saveConversation(newConversation);

        // Prepare response headers and body
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("conversation_id", conversationId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("msToken", msToken)
                .body(responseBody);
    }

    @PostMapping(value = "/save_message", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> forwardRequest() {

        return chatService.getStreamAnswer();
    }
    //        System.out.println(targetUrl);
//        System.out.println(1111);

//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + apiToken);
//        headers.set("Content-Type", "application/json");
//        // 设置请求体
//        String requestBody = "{ \"chat_history\": [{ \"role\": \"user\", \"content\": \"输出333\" }, { \"role\": \"user\", \"content\": \"输出nnn\" }], \"bot_id\": \"7369475330840576001\", \"user\": \"7345091449232851969\", \"query\": \"111\",\"stream\": true}";
//
//        WebClient webClient = WebClient.create();
//        // 发送 POST 请求，并返回响应的Flux
//        Flux<String> res = webClient.post()
//                .uri(targetUrl)
//                .headers(httpHeaders -> httpHeaders.addAll(headers))
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToFlux(String.class);
//
//        return res;

    /*return webSocketService.simulateStreamResponse();*/

    private StreamData convertToStreamData(String data) {
        try {
            return objectMapper.readValue(data, StreamData.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert data to StreamData", e);
        }
    }
}
