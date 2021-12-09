package com.example.community.controller;

import com.example.community.dao.MessageRepository;
import com.example.community.dao.UserRepository;
import com.example.community.entity.Message;
import com.example.community.entity.Question;
import com.example.community.entity.QuestionFollow;
import com.example.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true",maxAge = 3600)
@RestController
public class MessageController {
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;
    @PostMapping("/sendMessage")
    public Map<String, Object> deleteFollowQuestion(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            String userID = (String) req.get("userID");
            String message = (String) req.get("Message");
            String receiverId = (String) req.get("receiverId");
            Message message1 = new Message();
            message1.setContent(message);
            message1.setReceiverID(receiverId);
            message1.setSenderID(userID);
            message1.setViewed(false);
            message1.setType(3);
            message1.setSendTime(new Timestamp(System.currentTimeMillis()));
            messageRepository.save(message1);
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }
    }

    public List<Message> sortMessagesByTime(List<Message> messages){
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                Timestamp timestamp1 = o1.getSendTime();
                Timestamp timestamp2 = o2.getSendTime();
                return timestamp1.compareTo(timestamp2);
            }
        });
        return messages;
    }

    public List<Message> sortMessagesByTimeDesc(List<Message> messages){
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                Timestamp timestamp1 = o1.getSendTime();
                Timestamp timestamp2 = o2.getSendTime();
                return timestamp2.compareTo(timestamp1);
            }
        });
        return messages;
    }

    public Map<String,Object> putMessageMap(Message message){
        Map<String,Object> messageMap = new HashMap<>();
        String strDateFormat = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        messageMap.put("time",sdf.format(message.getSendTime()));
        messageMap.put("senderId",message.getSenderID());
        messageMap.put("receiverId",message.getReceiverID());
        messageMap.put("message",message.getContent());
        messageMap.put("isRead",message.getViewed());
        return messageMap;
    }

    @PostMapping("/getAllMessage")
    public Map<String, Object> getAllMessage(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            String userID = (String) req.get("userID");
            List<Map<String,Object>> personList = new ArrayList<>();
            //先根据userID，找到与这个人相关的收发信息
            //作为发送者发送的huo作为接收者收到的
            List<Message> mList = messageRepository.findAllBySenderIDOrReceiverID(userID,userID);
            mList = sortMessagesByTimeDesc(mList);
            List<String> contactPersonID = new ArrayList<>();
            for (Message message : mList){
                System.out.println(message.getMessageID());
                String id = message.getSenderID();
                if(id.equals(userID))id = message.getReceiverID();
                if(!contactPersonID.contains(id)){
                    contactPersonID.add(id);
                }
            }
            System.out.println(contactPersonID);
            for(String contactID : contactPersonID){
                Map<String,Object> personMap = new HashMap<>();
                List<Map<String,Object>> messageList = new ArrayList<>();
                boolean isRead = true;
                List<Message> messageContactList = messageRepository.findAllBySenderIDAndReceiverIDOrReceiverIDAndSenderID(userID,contactID,userID,contactID);
                messageContactList = sortMessagesByTime(messageContactList);
                for(Message m : messageContactList){
                    if(!m.getViewed()){
                        isRead = false;
                    }
                    messageList.add(putMessageMap(m));
                }
                User user = userRepository.findByUserID(contactID);
                personMap.put("personId",user.getUserID());
                personMap.put("personName",user.getUsername());
                personMap.put("avatar",user.getImage());
                personMap.put("isRead",isRead);
                personMap.put("messageList",messageList);
                personList.add(personMap);
            }

            response.put("personList",personList);
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }
    }

    @PostMapping("/readMessage")
    public Map<String, Object> readMessage(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            String userID = (String) req.get("userID");
            String contactID = (String) req.get("personId");
            List<Message> messageContactList = messageRepository.findAllBySenderIDAndReceiverIDOrReceiverIDAndSenderID(userID,contactID,userID,contactID);
            for(Message message : messageContactList){
                message.setViewed(true);
                messageRepository.save(message);
            }
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }
    }

}