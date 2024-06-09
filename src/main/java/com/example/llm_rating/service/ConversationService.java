package com.example.llm_rating.service;

import com.example.llm_rating.model.*;
import com.example.llm_rating.repository.ConversationRepository;
import com.example.llm_rating.repository.MessageDetailRepository;
import com.example.llm_rating.repository.MessageRepository;
import com.example.llm_rating.repository.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.llm_rating.model.Conversation.Message;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageDetailRepository messageDetailRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private MessageRepository messageRepository;

//    public ResponseEntity<?> getMessages(String msToken, ConversationRequest request) {
//        // 检查 msToken 是否有效，略去
//
//        // 根据 conversation_id 查询消息
//        List<Message> messages = conversationRepository.findByConversationId(request.getConversationId());
//
//        // 如果未找到消息，返回 404 Not Found
//        if (messages.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("未找到该会话");
//        }
//
//        // 构建返回消息列表
//        List<MessageResponse> messageResponses = buildMessageResponses(request.getConversationId());
//
//        // 返回消息列表
//        return ResponseEntity.ok().body(messageResponses);
//    }


    public List<MessageResponse> buildMessageResponses(String conversationId) {
        List<MessageResponse> messageResponses = new ArrayList<>();
        List<MessageDetail> messageDetails = getMessageDetailsByConversationId(conversationId);
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

    public void changeConversationId(String conversationId,String modelName,String userId) {
        Optional<Conversation> savedata = conversationRepository.findById(conversationId);
        if (savedata.isPresent()) {
            Conversation conversation = savedata.get();
            conversation.setConversationId(conversationId);
            conversation.setModelName(modelName);
            conversation.setUserId(userId);

            ModelService modelService = new ModelService(modelRepository);
            System.out.println(modelName);
            System.out.println("!!!!!!!!!!!!!!!");
            Model model = modelService.getModelByModelName(modelName);
            conversation.setModelId(model.getId());
            conversationRepository.save(conversation); // Save the conversation object, not Optional
        } else {
            // Optional: Log or handle the case where the conversation is not found
            System.err.println("Conversation with ID " + conversationId + " not found.");
        }
    }


    public String ConversationIdGetIdService(String conversationId){
        Optional<Conversation> data = conversationRepository.findByConversationId(conversationId);
        return data.get().getId();
    }


    @Autowired
    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public List<Conversation> getConversationsByUserId(String userId) {
        System.out.println(userId);

//        return conversationRepository.findByUserId(userId);
    return conversationRepository.findByUserId(userId);

    }






    // 根据消息对象的索引值从数据库中查询对应的消息详情



    public List<MessageDetail> getMessageDetailsByConversationId(String conversationId) {
        Optional<Conversation> conversation = conversationRepository.findByConversationId(conversationId);

        if (conversation.isPresent()) {
            List<Message> messages = conversation.get().getMessages();
            List<MessageDetail> messageDetails = new ArrayList<>();

            for (Message message : messages) {
                System.out.println(message.getMessageId());
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
        if (conversation!= null) {
            conversation.setTitle(newTitle);
            // 将更新后的会话保存回数据库
            conversationRepository.save(conversation);
        }

        return conversation;
    }
}
