package com.example.llm_rating.controller;

import com.example.llm_rating.model.MessageResponse;
import com.example.llm_rating.model.StreamData;
import com.example.llm_rating.service.ChatService;
import com.example.llm_rating.service.ConversationService;
import com.example.llm_rating.service.ModelService;
import com.example.llm_rating.service.VoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/api")
public class BattleController {


    public static final String PATH = "/conversation/title";
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;
    private final ConversationService conversationService;
    private final ModelService modelService;
    private final ObjectMapper objectMapper;
    private final VoteService voteService;
    @Value("${target.api.url}")
    private String targetUrl;
    @Value("${api.token}")
    private String apiToken;

    public BattleController(ChatService chatService, ConversationService conversationService, ModelService modelService, ObjectMapper objectMapper, VoteService voteService) {
        this.chatService = chatService;
        this.conversationService = conversationService;
        this.modelService = modelService;
        this.objectMapper = objectMapper;
        this.voteService = voteService;
    }


    @PostMapping(value = "/battle/create")
    public ResponseEntity<String> createConversation(Authentication auth) {
        String userId = auth != null ? auth.getName() : null;

        List<String> models = Arrays.asList("gpt4", "gemini1.5 flash", "Gemini 1.5 pro", "gpt4o","Moonshot 8k","豆包","通义千问","miniMax","gpt3.5");

        String battleId = UUID.randomUUID().toString();
        List<String> id = conversationService.getNewRandomConversationId(models);

        String selectedModel1 = id.get(0);
        String selectedModel2 = id.get(1);

        Map<String, String> res1 = conversationService.createBattleConversation(selectedModel1, userId, battleId);
        String conversationId1 = res1.get("conversation_id");
        Map<String, String> res2 = conversationService.createBattleConversation(selectedModel2, userId, battleId);
        String conversationId2 = res2.get("conversation_id");

        // Create a map with the desired output structure
        Map<Object, String> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("conversation_id", battleId);

        responseBody.put("message", "错误信息描述");

        return ResponseEntity.status(HttpStatus.OK)
                .body(battleId);
    }


    @PostMapping(value = "/battle/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> newchat(@RequestBody Map<String, Object> requestBody, Authentication auth) throws Exception {
        String userId = auth != null ? auth.getName() : null;


        String battleId = (String) requestBody.get("battle_id_t");
        List<String> conversationList = chatService.getIdFromBattleId(battleId);

        String conversationId1 = conversationList.get(0);
        String conversationId2 = conversationList.get(1);
        String contentType = (String) requestBody.get("content_type");

        String query = (String) requestBody.get("query");


        List<MessageResponse> history1 = conversationService.buildBattleMessageResponses(conversationId1);
        System.out.println(history1);
        System.out.println("History1");
        List<MessageResponse> history2 = conversationService.buildBattleMessageResponses(conversationId2);
        String modelName1 = chatService.getModlename(conversationId1).get("modelName").toString();
        String modelName2 = chatService.getModlename(conversationId2).get("modelName").toString();


        Flux<String> resa = chatService.getStreamAnswer2(contentType, query, history1, conversationId1, modelName1, "model_a");

        Flux<String> resb = chatService.getStreamAnswer2(contentType, query, history2, conversationId2, modelName2, "model_b");
        Flux<String> res = Flux.merge(resa, resb).concatWith(Flux.just("{\"event\": \"done\"}"));

        return res;
    }


    @PostMapping(value = "/v1/vote/sidebyside_anonymous")
    public ResponseEntity<HashMap<String, Object>> givingVote(@RequestBody Map<String, Object> requestBody, Authentication auth) throws Exception {
        String userId = auth != null ? auth.getName() : null;
        HashMap<String, Object> hashmap = new HashMap<>();
        String battleId = (String) requestBody.get("battle_id");
        List<String> conversationList = chatService.getIdFromBattleId(battleId);

        String vote = (String) requestBody.get("type");
        String conversationId1 = conversationList.get(0);
        String conversationId2 = conversationList.get(1);
        List<List> history1 = conversationService.battleMessageResponses(conversationId1);
        List<List> history2 = conversationService.battleMessageResponses(conversationId2);
        Map modela = chatService.getModlename(conversationId1);

        String modelA = (String) modela.get("modelName");
        Map modelb = chatService.getModlename(conversationId2);
        String modelB = (String) modelb.get("modelName");


        List<Map<String, Object>> states = new ArrayList<>();

        Map<String, Object> state1 = new HashMap<>();
        state1.put("conv_id", conversationId1);
        Map<String, Object> state9 = new HashMap<>();
        state1.put("model", state9);
        state9.put("model_name", modela.get("modelName"));
        state9.put("model_id", modela.get("modelId"));

        state1.put("messages", history1);
        state1.put("offset", 0);

        Map<String, Object> state2 = new HashMap<>();
        state2.put("conv_id", conversationId2);
        Map<String, Object> state10 = new HashMap<>();
        state2.put("model", state10);
        state10.put("model_name", modelb.get("modelName"));
        state10.put("model_id", modelb.get("modelId"));
        state2.put("model_name", modelB);

        state2.put("messages", history2);
        state2.put("offset", 0);

        states.add(state1);
        states.add(state2);

        HashMap<String, Object> response = new HashMap<>();
        response.put("type", vote);
        response.put("models", Arrays.asList(modelA, modelB));
        response.put("states", states);
        response.put("uid", userId);
        response.put("tstamp", new Date());
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDateTime = now.format(formatter);
        String fileName = formattedDateTime + "-conv.json";
        voteService.appendToJsonFile("./logs/"+fileName, response);

        return ResponseEntity.ok().body(response);
    }


    private StreamData convertToStreamData(String data) {
        try {
            return objectMapper.readValue(data, StreamData.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert data to StreamData", e);
        }
    }
}
