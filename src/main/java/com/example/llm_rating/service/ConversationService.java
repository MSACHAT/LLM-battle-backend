package com.example.llm_rating.service;

import com.example.llm_rating.model.*;
import com.example.llm_rating.model.Conversation.Message;
import com.example.llm_rating.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;


    private final BattleConversationRepository battleConversationRepository;

    private final MessageDetailRepository messageDetailRepository;

    private final ModelRepository modelRepository;

    private final MessageRepository messageRepository;

    public static List<String> getNewRandomConversationId(List<String> modelList) {
        if (modelList.size() < 2) {
            throw new IllegalArgumentException("List must contain at least two elements");
        }

        Random random = new Random();
        List<String> result = new ArrayList<>();
        int index1 = random.nextInt(modelList.size());
        result.add(modelList.get(index1));

        // 从剩下的元素中随机选择第二个元素
        for (int i = 0; i < modelList.size(); i++) {
                int index2 = random.nextInt(modelList.size() );
            System.out.print("index2:");
            System.out.println(index2);
            if (index2 != index1) {
                result.add(modelList.get(index2));
                break; // 找到第二个元素后退出循环
            }
        }

        return result;
    }

//    Random random = new Random();
//    List<String> result = new ArrayList<>();
//    int index1 = random.nextInt(modelList.size());
//        result.add(modelList.get(index1));
//
//
//    // 从剩下的元素中随机选择第二个元素
//        modelList.remove(index1);
//    int index2 = random.nextInt(modelList.size() - 1);
//        result.add(modelList.get(index2));
//
//        System.out.println(result);
//
//        return result;

    public Map<String, String> createConversation(String modelName, String userId) {

        if (modelName == null || modelName.isEmpty()) {
            return null;
        }

        // Generate a unique conversation ID
        String conversationId = UUID.randomUUID().toString();
        // Generate a unique msToken
        String msToken = UUID.randomUUID().toString();

        // Create a new conversation and save it to the database
        Conversation newConversation = new Conversation(conversationId);
        newConversation.setModelId(modelName);
        saveConversation(newConversation);
        String newId = ConversationIdGetIdService(conversationId);

        // Prepare response headers and body
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("conversation_id", newId);

        changeConversationId(newId, modelName, userId);


        return responseBody;
    }

    public Map<String, String> createBattleConversation(String modelName, String userId, String battleId) {
        if (modelName == null || modelName.isEmpty()) {
            return null;
        }

        // Create a new BattleConversation and save it to the database
        BattleConversation newConversation = new BattleConversation();// Assuming you want to set the ID manually

        newConversation.setModelName(modelName); // Assuming modelName is also the model name
        newConversation.setUserId(userId);
        ModelService modelService = new ModelService(modelRepository);
        Model model = modelService.getModelByModelName(modelName);
        newConversation.setModelId(model.getId());
        newConversation.setBattleId(battleId);

        // Save the new conversation to the databas
        battleConversationRepository.save(newConversation);

        // Prepare response headers and body
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("conversation_id", newConversation.getId());
        return responseBody;
    }


    public List<MessageResponse> buildMessageResponses(String conversationId) {
        List<MessageResponse> messageResponses = new ArrayList<>();
        List<MessageDetail> messageDetails = getMessageDetailsByConversationId(conversationId);
        ObjectMapper objectMapper = new ObjectMapper();


        JsonNode allMessageResponsesJsonNode = null;

        allMessageResponsesJsonNode = objectMapper.valueToTree(messageDetails);
        for (MessageDetail messageDetail : messageDetails) {
            MessageResponse responseDetail = new MessageResponse();
            responseDetail.setRole(messageDetail.getRole());
            responseDetail.setContent(messageDetail.getContent());
            responseDetail.setContentType(messageDetail.getContentType());
            responseDetail.setMessageId(messageDetail.getId());

            messageResponses.add(responseDetail);
        }
        return messageResponses;
    }

    public List<MessageResponse> buildBattleMessageResponses(String conversationId) {
        List<MessageResponse> messageResponses = new ArrayList<>();
        List<MessageDetail> messageDetails = getMessageDetailsByBattleConversationId(conversationId);
        ObjectMapper objectMapper = new ObjectMapper();


        JsonNode allMessageResponsesJsonNode = null;

        allMessageResponsesJsonNode = objectMapper.valueToTree(messageDetails);
        for (MessageDetail messageDetail : messageDetails) {
            MessageResponse responseDetail = new MessageResponse();
            responseDetail.setRole(messageDetail.getRole());
            responseDetail.setContent(messageDetail.getContent());
            responseDetail.setContentType(messageDetail.getContentType());
            responseDetail.setMessageId(messageDetail.getId());

            messageResponses.add(responseDetail);
        }
        return messageResponses;
    }


    public List<List> battleMessageResponses(String conversationId) {
        List<List> messageResponses = new ArrayList<>();
        List<MessageDetail> messageDetails = getMessageDetailsByBattleConversationId(conversationId);
        for (MessageDetail messageDetail : messageDetails) {
            List newarry = new ArrayList();
            newarry.add(messageDetail.getRole());
            newarry.add(messageDetail.getContent());
            messageResponses.add(newarry);
        }
        return messageResponses;
    }

    public void deleteConversationWithMessages(String conversationId) {
        // 删除会话
        conversationRepository.deleteById(conversationId);
        // 根据会话ID删除所有消息
        List<String> messageIds = conversationRepository.findById(conversationId)
                .map(conversation -> conversation.getMessages().stream().map(Message::getMessageId).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        try {
            messageRepository.deleteAllById(messageIds);
        } catch (Exception e) {
            // 处理异常，例如记录日志
        }
    }

    public void changeConversationId(String conversationId, String modelName, String userId) {
        Optional<Conversation> savedata = conversationRepository.findById(conversationId);
        if (savedata.isPresent()) {
            Conversation conversation = savedata.get();
            conversation.setConversationId(conversationId);
            conversation.setModelName(modelName);
            conversation.setUserId(userId);

            ModelService modelService = new ModelService(modelRepository);
            Model model = modelService.getModelByModelName(modelName);
            conversation.setModelId(model.getId());
            conversationRepository.save(conversation); // Save the conversation object, not Optional
        } else {
            // Optional: Log or handle the case where the conversation is not found
            System.err.println("Conversation with ID " + conversationId + " not found.");
        }
    }


    public String ConversationIdGetIdService(String conversationId) {
        Optional<Conversation> data = conversationRepository.findByConversationId(conversationId);
        return data.get().getId();
    }

    public List<Conversation> getConversationsByUserId(String userId) {
        return conversationRepository.findByUserId(userId);

    }


    // 根据消息对象的索引值从数据库中查询对应的消息详情


    public List<MessageDetail> getMessageDetailsByConversationId(String conversationId) {
        Optional<Conversation> conversation = conversationRepository.findById(conversationId);

        if (conversation.isPresent()) {
            List<Conversation.Message> messages = conversation.get().getMessages();
            List<MessageDetail> messageDetails = new ArrayList<>();

            for (Conversation.Message message : messages) {
                Optional<MessageDetail> messageDetailOptional = messageDetailRepository.findById(message.getMessageId());

                messageDetailOptional.ifPresent(messageDetails::add);
            }

            return messageDetails;
        }

        return new ArrayList<>();
    }


    public List<MessageDetail> getMessageDetailsByBattleConversationId(String conversationId) {
        Optional<BattleConversation> conversation = battleConversationRepository.findById(conversationId);

        if (conversation.isPresent()) {
            List<BattleConversation.Message> messages = conversation.get().getMessages();
            List<MessageDetail> messageDetails = new ArrayList<>();

            for (BattleConversation.Message message : messages) {
                Optional<MessageDetail> messageDetailOptional = messageDetailRepository.findById(message.getMessageId());

                messageDetailOptional.ifPresent(messageDetails::add);
            }

            return messageDetails;
        }

        return new ArrayList<>();
    }


    public void handleIncomingData(StreamData data) {
        Conversation conversation = getOrCreateConversation(data.getConversationId());
        updateConversation(conversation, data);
        conversationRepository.save(conversation);
    }

    public void saveConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }


    private Conversation getOrCreateConversation(String conversationId) {
        return conversationRepository.findById(conversationId).orElse(new Conversation(conversationId));
    }

    public Optional<Conversation> getConversation() {
        return conversationRepository.findByConversationId("1");
    }

    private void updateConversation(Conversation conversation, StreamData data) {
        StreamData.StreamMessage streamMessage = data.getMessage();
        Message message = new Message();
        message.setIndex(streamMessage.getIndex());
        message.setMessageId(streamMessage.getMessageId());

        conversation.getMessages().add(message);
    }

    public Conversation updateConversationTitle(String conversationId, String newTitle) {
        // 首先，根据 conversationId 从数据库中查找对应的会话
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElse(null);

        // 如果找到了会话，则更新其标题
        if (conversation != null) {
            conversation.setTitle(newTitle);
            // 将更新后的会话保存回数据库
            conversationRepository.save(conversation);
        }

        return conversation;
    }
}
