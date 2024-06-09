package com.example.llm_rating.controller;

import com.example.llm_rating.model.*;
import com.example.llm_rating.service.ChatService;
import com.example.llm_rating.service.ConversationService;
import org.springframework.security.core.Authentication;
import com.example.llm_rating.service.ModelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.testng.annotations.IFactoryAnnotation;
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


    public static final String PATH = "/conversation/title";
    @Value("${target.api.url}")
    private String targetUrl;

    @Value("${api.token}")
    private String apiToken;

    private final ChatService chatService;
    private final ConversationService conversationService;
    private final ModelService modelService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);


    public ChatController(ChatService chatService, ConversationService conversationService, ModelService modelService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.conversationService = conversationService;
        this.modelService = modelService;
        this.objectMapper = objectMapper;
    }
//    @GetMapping(value = "/conversations")
//    public ResponseEntity<List>

    @PostMapping("/conversation/break_message")
    public ResponseEntity stopedRequest(){
        System.out.println("正在运行/conversation/break_message");

        chatService.stopped();


        return ResponseEntity.ok("已停止");
    }

    @GetMapping(value = "/models", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ModelResponse>> getAllModels() {
        System.out.println("正在运行/models");
        List<Model> models = modelService.getAllModels();
        List<ModelResponse> simpleModels = models.stream()
                .map(model -> new ModelResponse(model.getId(), model.getModelName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(simpleModels);
    }
    @GetMapping("/model/{id}")
    public ResponseEntity<ModelResponse> getModelById(@PathVariable String id) {
        System.out.println("正在运行/model/{id}");
        Model model = modelService.getModelById(id);
        if (model != null) {
            ModelResponse resmodel = new ModelResponse(model.getId(), model.getModelName());
                    return ResponseEntity.ok(resmodel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping(value = "/conversations")
    public ResponseEntity<List<Map<String, Object>>> ConversationsByUserId(Authentication auth) {
        System.out.println("正在运行/conversations");
        String userId = auth != null ? auth.getName() : null;

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Conversation> conversations = conversationService.getConversationsByUserId(userId);
        System.out.println(111);
        System.out.println(conversations);
        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(conversations);
//            System.out.println(json);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        if (conversations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Map<String, Object>> response = conversations.stream()
                .map(conversation -> {
                    Map<String, Object> map = new HashMap<>();

                    if (conversation.getLastMessageTime() == null) {
                        map.put("conversation_id", conversation.getConversationId());
                        map.put("title", conversation.getTitle());
                        map.put("last_message_time",null);
                        map.put("bot_name",conversation.getModelName());
                    } else {
                        // 处理没有有效消息时间的情况
                        map.put("conversation_id", conversation.getConversationId());
                        map.put("title", conversation.getTitle());
                        map.put("last_message_time",conversation.getLastMessageTime());
                        map.put("bot_name",conversation.getModelName());
                    }
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }




    @GetMapping(value = "/conversation/{conversation_id}/get_message_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getMessageList(@PathVariable("conversation_id") String conversationId,
                                                              @RequestParam("pageSize") int pageSize,
                                                              @RequestParam("pageNum") int pageNum,
                                                              Authentication auth) {
        System.out.println("正在运行/conversation/{conversation_id}/get_message_list");
        String userId = auth != null ? auth.getName() : null;
        // 使用 conversationId, pageSize 和 pageNum 进行相应的处理



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

    @PatchMapping(PATH)
    public ResponseEntity<?> updateConversationTitle(@RequestBody Map<String, String> request) {

            String conversationId = request.get("conversation_id");
            String title = request.get("title");


            Conversation updatedConversation = conversationService.updateConversationTitle(conversationId,title);
            if (updatedConversation == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedConversation);

    }



    @GetMapping(value = "/test")
    public ResponseEntity<HttpStatus> createConversation1() {;
        System.out.println("正在运行/test");
        return new ResponseEntity(HttpStatus.OK);
    }



    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity deleteConversation(@PathVariable String conversationId ) {
        try {
            System.out.println("正在运行Delate conversation");
            conversationService.deleteConversationWithMessages(conversationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {

                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }



    @PostMapping (value = "/conversation/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String>newchat(@RequestBody Map<String, String> requestBody) {
        System.out.println("正在运行/conversation/chat");
        String contentType = requestBody.get("content_type");
        String conversationId = requestBody.get("conversation_id");
//        Map<String, Object> extra = request.getExtra();
         String query = requestBody.get("query");

        if (conversationId == null || conversationId.isEmpty()) {
            System.out.println("conversationId要后端新建吗");
        }
        System.out.println(1111);

        List<MessageResponse> history = conversationService.buildMessageResponses(conversationId);

        System.out.println(222);
        if (history.isEmpty()){
            conversationService.updateConversationTitle(conversationId,query);
        }

        System.out.println(history);

        // 如果返回结果为 null 或未找到，则返回空 Flux
        if (history == null) {
            return Flux.empty();
        }
        System.out.println(333);
        return chatService.getStreamAnswer1(contentType, query, history,conversationId);

    }

    @PostMapping(value = "/conversation/create_conversation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createConversation(@RequestBody Map<String, String> requestBody,Authentication auth) {
        System.out.println("正在运行/conversation/create_conversation");

        String userId = auth != null ? auth.getName() : null;



        String modelName = requestBody.get("model_name");
        if (modelName == null || modelName.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Generate a unique conversation ID
        String conversationId = UUID.randomUUID().toString();
        // Generate a unique msToken
        String msToken = UUID.randomUUID().toString();

        // Create a new conversation and save it to the database
        Conversation newConversation = new Conversation(conversationId);
        newConversation.setModelId(modelName);
        conversationService.saveConversation(newConversation);
        String newId  = conversationService.ConversationIdGetIdService(conversationId);

        // Prepare response headers and body
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("conversation_id",newId);

        conversationService.changeConversationId(newId,modelName,userId);

        System.out.println(responseBody.toString());


        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

//    @PostMapping(value = "/save_message", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> forwardRequest() {
//
//        return chatService.getStreamAnswer();
//    }

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
