package com.example.llm_rating.controller;

import com.example.llm_rating.model.*;
import com.example.llm_rating.service.ChatService;
import com.example.llm_rating.service.ConversationService;
import com.example.llm_rating.service.ModelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class ChatController {

    public static final String PATH = "/conversation/title";
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final ConversationService conversationService;
    private final ModelService modelService;
    private final ObjectMapper objectMapper;
    @Value("${target.api.url}")
    private String targetUrl;
    @Value("${api.token}")
    private String apiToken;

    public ChatController(ChatService chatService, ConversationService conversationService, ModelService modelService,
                          ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.conversationService = conversationService;
        this.modelService = modelService;
        this.objectMapper = objectMapper;
    }
    // @GetMapping(value = "/conversations")
    // public ResponseEntity<List>

    @PostMapping("/conversation/break_message")
    public ResponseEntity stopedRequest(@RequestBody Map<String, String> requestBody) {

        String conversationId = requestBody.get("conversation_id");

        chatService.stopped(conversationId);

        return ResponseEntity.ok("已停止");
    }

    @PostMapping("/battle/break_message")
    public ResponseEntity battlestopedRequest(@RequestBody Map<String, String> requestBody) {

        String battleId = requestBody.get("battle_id");
        List<String> conversationList = chatService.getIdFromBattleId(battleId);

        String conversationId1 = conversationList.get(0);
        String conversationId2 = conversationList.get(1);
        chatService.stopped(conversationId1);
        chatService.stopped(conversationId2);

        return ResponseEntity.ok("已停止");
    }

    @GetMapping(value = "/models", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ModelResponse>> getAllModels() {
        List<Model> models = modelService.getAllModels();
        List<ModelResponse> simpleModels = models.stream()
                .map(model -> new ModelResponse(model.getId(), model.getModelName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(simpleModels);
    }

    @GetMapping("/model/{id}")
    public ResponseEntity<ModelResponse> getModelById(@PathVariable String id) {
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
        String userId = auth != null ? auth.getName() : null;

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Conversation> conversations = conversationService.getConversationsByUserId(userId);
        Collections.reverse(conversations);
        ObjectMapper objectMapper = new ObjectMapper();

        if (conversations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Map<String, Object>> response = conversations.stream()
                .map(conversation -> {
                    Map<String, Object> map = new HashMap<>();

                    if (conversation.getLastMessageTime() == null) {
                        map.put("conversation_id", conversation.getConversationId());
                        map.put("title", conversation.getTitle());
                        map.put("last_message_time", null);
                        map.put("bot_name", conversation.getModelName());
                    } else {
                        // 处理没有有效消息时间的情况
                        map.put("conversation_id", conversation.getConversationId());
                        map.put("title", conversation.getTitle());
                        map.put("last_message_time", conversation.getLastMessageTime());
                        map.put("bot_name", conversation.getModelName());
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
        String userId = auth != null ? auth.getName() : null;
        // 使用 conversationId, pageSize 和 pageNum 进行相应的处理
        if (conversationId == null || conversationId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<MessageResponse> allMessageResponses = conversationService.buildMessageResponses(conversationId);
        // 反转列表以使最新的消息在前
        Collections.reverse(allMessageResponses);

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


        Conversation updatedConversation = conversationService.updateConversationTitle(conversationId, title);
        if (updatedConversation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("");

    }

    @GetMapping(value = "/test")
    public ResponseEntity<HttpStatus> createConversation1() {

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/conversation/{conversationId}")
    public ResponseEntity deleteConversation(@PathVariable String conversationId) {
        try {

            conversationService.deleteConversationWithMessages(conversationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping(value = "/conversation/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> newchat(@RequestBody Map<String, String> requestBody) {
        String contentType = requestBody.get("content_type");
        String conversationId = requestBody.get("conversation_id");
        // Map<String, Object> extra = request.getExtra();
        String query = requestBody.get("query");

        if (conversationId == null || conversationId.isEmpty()) {

        }


        List<MessageResponse> history = conversationService.buildMessageResponses(conversationId);


        if (history.isEmpty()) {
            conversationService.updateConversationTitle(conversationId, query);
        }


        // 如果返回结果为 null 或未找到，则返回空 Flux
        if (history == null) {
            return Flux.empty();
        }

        return chatService.getStreamAnswer(contentType, query, history, conversationId);

    }

    @PostMapping(value = "/conversation/create_conversation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createConversation(@RequestBody Map<String, String> requestBody,
                                                                  Authentication auth) {


        String userId = auth != null ? auth.getName() : null;

        String modelName = requestBody.get("model_name");

        return ResponseEntity.status(HttpStatus.OK)
                .body(conversationService.createConversation(modelName, userId));
    }

    private StreamData convertToStreamData(String data) {
        try {
            return objectMapper.readValue(data, StreamData.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert data to StreamData", e);
        }
    }
}
