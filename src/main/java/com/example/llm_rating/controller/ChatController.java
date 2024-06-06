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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
//    @GetMapping(value = "/conversations")
//    public ResponseEntity<List>

    @PostMapping("/conversation/b")
    public String stopedRequest(){
        System.out.println(73841732);
        chatService.stopped();
        System.out.println(273647362);
        return "ok";
    }

    @PostMapping(value = "/conversations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getConversationsByUserId(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("userId");
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Conversation> conversations = conversationService.getConversationsByUserId(userId);

        List<Map<String, Object>> response = conversations.stream()
                .map(conversation -> {
                    Map<String, Object> map = new HashMap<>();
                    Conversation.Message lastMessage = conversation.getMessages().stream()
                            .max((m1, m2) -> m1.getLastMessageTime().compareTo(m2.getLastMessageTime()))
                            .orElse(null);
                    if (lastMessage != null) {
                        map.put("conversation_id", conversation.getConversationId());
                        map.put("title", lastMessage.getTitle());
                        map.put("last_message_time", lastMessage.getLastMessageTime());
                        map.put("bot_name", lastMessage.getModelId().equals("model1") ? "gpt-3.5" : "gpt-4");
                    }
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @GetMapping(value = "/conversation/get_message_list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getMessageList(@RequestBody Map<String, String> requestBody) {

        String str1 = requestBody.get("pageNum");
        String str2 = requestBody.get("pageSize");
        int pageNum = Integer.parseInt(str1);
        int pageSize = Integer.parseInt(str2);
        String conversationId = requestBody.get("conversation_id");

        if (conversationId == null || conversationId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<MessageResponse> allMessageResponses = conversationService.buildMessageResponses(conversationId);

        int totalItems = allMessageResponses.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        int fromIndex = pageNum * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        if (fromIndex >= totalItems) {
            return ResponseEntity.ok(Collections.emptyMap());
        }

        List<MessageResponse> pagedMessageResponses = allMessageResponses.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("data", pagedMessageResponses);
        response.put("total", totalItems);
        response.put("pageSize", pageSize);
        response.put("currentPage", pageNum);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
    }


    @GetMapping(value = "/test")
    public ResponseEntity<HttpStatus> createConversation1() {;
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping (value = "/conversation/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String>newchat(@RequestBody Map<String, String> requestBody) {
        String contentType = requestBody.get("content_type");
        String conversationId = requestBody.get("convsersation_id");
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
