package com.example.llm_rating.controller;

import com.example.llm_rating.model.*;
import com.example.llm_rating.model.DTO.BattleMessageResponse;
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
public class BattleController {


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


    public BattleController(ChatService chatService, ConversationService conversationService, ModelService modelService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.conversationService = conversationService;
        this.modelService = modelService;
        this.objectMapper = objectMapper;
    }
//    @GetMapping(value = "/conversations")
//    public ResponseEntity<List>


    @PostMapping(value = "/battle/create")
    public ResponseEntity<String> createConversation(Authentication auth) {
        String userId = auth != null ? auth.getName() : null;

        List<String> models = Arrays.asList("coze");
        Random random = new Random();
        String battleId = UUID.randomUUID().toString();

        String selectedModel1 = models.get(random.nextInt(models.size()));
        String selectedModel2 = models.get(random.nextInt(models.size()));

        Map<String, String> res1 = conversationService.createBattleConversation(selectedModel1, userId,battleId);
        String conversationId1 = res1.get("conversation_id");
        Map<String, String> res2 = conversationService.createBattleConversation(selectedModel2, userId,battleId);
        String conversationId2 = res2.get("conversation_id");

        // Create a map with the desired output structure
        Map<Object,String> responseBody = new HashMap<>();
        responseBody.put("status","success");
        responseBody.put("conversation_id", battleId);

        responseBody.put("message","错误信息描述");

        return ResponseEntity.status(HttpStatus.OK)
                .body(battleId);
    }



    @PostMapping (value = "/battle/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String>newchat(@RequestBody Map<String, Object> requestBody,Authentication auth) throws Exception {
        String userId = auth != null ? auth.getName() : null;
        String modelName = "coze";

        String battleId = (String) requestBody.get("battle_id");
        List<String>conversationList = chatService.getIdFromBattleId(battleId);
        System.out.println(conversationList);
//
        String conversationId1 = conversationList.get(0);
        String conversationId2 = conversationList.get(1);
        String contentType = (String)requestBody.get("content_type");

//        Map<String, Object> extra = request.getExtra();
        String query = (String) requestBody.get("query");

        System.out.println(1111);

        List<MessageResponse> history1 = conversationService.buildMessageResponses(conversationId1);

        List<MessageResponse> history2 = conversationService.buildMessageResponses(conversationId2);

        System.out.println("history1");
        System.out.println(history1);
        System.out.println("history2");
        System.out.println(history2);

        Flux<String> resa = chatService.getStreamAnswer2(contentType, query, history1 ,conversationId1, modelName,"model_a");

        Flux<String> resb = chatService.getStreamAnswer2(contentType, query, history2 ,conversationId2, modelName,"model_b");
        Flux<String> res = Flux.merge(resa, resb).concatWith(Flux.just("{\"event\": \"done\"}"));

        return res;
    }


    @PostMapping (value = "/v1/vote/sidebyside_anonymous")
    public ResponseEntity<HashMap<String,Object>>givingVote(@RequestBody Map<String, Object> requestBody,Authentication auth) throws Exception {
        String userId = auth != null ? auth.getName() : null;
        HashMap<String, Object> hashmap = new HashMap<>();
        String battleId = (String) requestBody.get("battle_id");
        List<String>conversationList = chatService.getIdFromBattleId(battleId);
        System.out.println(conversationList);

        String vote = (String) requestBody.get("type");
        String conversationId1 = conversationList.get(0);
        String conversationId2 = conversationList.get(1);
        List<List> history1 = conversationService.battleMessageResponses(conversationId1);
        List<List> history2 = conversationService.battleMessageResponses(conversationId2);
        Map modela = chatService.getModlename(conversationId1);
        System.out.println(modela.get(1));
        System.out.println("modela");
        String modelA = (String) modela.get("modelName");
        Map modelb = chatService.getModlename(conversationId2);
        String modelB = (String) modelb.get("modelName");
        System.out.println(history1);


        List<Map<String, Object>> states = new ArrayList<>();

        Map<String, Object> state1 = new HashMap<>();
        state1.put("conv_id", conversationId1);
        Map<String, Object> state9 = new HashMap<>();
        state1.put("model",state9);
        state9.put("model_name", modela.get("modelName"));
        state9.put("model_id", modela.get("modelId"));

        state1.put("messages", history1);
        state1.put("offset", 0);

        Map<String, Object> state2 = new HashMap<>();
        state2.put("conv_id", conversationId2);
        Map<String, Object> state10 = new HashMap<>();
        state2.put("model",state10);
        state10.put("model_name", modelb.get("modelName"));
        state10.put("model_id", modelb.get("modelId"));
        state2.put("model_name", modelB);

        state2.put("messages", history2);
        state2.put("offset", 0);

        states.add(state1);
        states.add(state2);

        HashMap<String, Object> response = new HashMap<>();
        response.put("type", "tievote");
        response.put("models", Arrays.asList(modelA, modelB));
        response.put("states", states);
        response.put("uid", userId);
        response.put("tstamp", new Date());

        System.out.println(response);

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
