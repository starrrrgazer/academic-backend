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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443",
        "https://localhost:8000","https://localhost:80","https://localhost:443",
        "http://121.36.60.6:8000","http://121.36.60.6:80","http://121.36.60.6:443",
        "https://121.36.60.6:8000","https://121.36.60.6:80","https://121.36.60.6:443"},allowCredentials = "true",maxAge = 3600)
@RestController
public class MessageController {
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;

    public byte[] getUserAvatar(String image){
        try {
            File avatar = new File(image);
            if(avatar.exists() && avatar.canRead()) {
                byte[] buffer = new byte[(int) avatar.length()];
                InputStream in = new FileInputStream(avatar);
                in.read(buffer);
                return buffer;
            }
            else {
                File dft = new File("./static/image/default.jpg");
                if(dft.exists()) {
                    byte[] buffer = new byte[(int) dft.length()];
                    InputStream in = new FileInputStream(dft);
                    in.read(buffer);
                    return buffer;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("getUserImage error");
            return new byte[0];
        }
        return new byte[0];
    }

    public void checkResponseMap(Map<String,Object> response)throws Exception{
        if(response.containsKey("status") && (int)response.get("status") != 1){
            throw new Exception("status is " + response.get("status"));
        }
    }

    public Map<String,Object> getUserByLogin(Map<String,Object> response){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);

        if(session.getAttribute("userID") == null){
            response.put("status",3);
        }
        else{
            String userID = (String) session.getAttribute("userID");
            response.put("userID",userID);
        }
        return response;
    }


    @PostMapping("/sendMessage")
    public Map<String, Object> sendMessage(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            String message = (String) req.get("Message");
            String receiverId = (String) req.get("receiverId");
            if(receiverId == null){
                String receiverName = (String) req.get("receiverName");
                User user = userRepository.findByUsername(receiverName);
                receiverId = user.getUserID();
            }
            Message message1 = new Message();
            message1.setContent(message);
            message1.setReceiverID(receiverId);
            message1.setSenderID(userID);
            message1.setViewed(false);
            if(req.containsKey("type")&& (int)req.get("type") != 3 ){
                message1.setType((int)req.get("type"));
            }
            else {
                message1.setType(3);
            }
            message1.setSendTime(new Timestamp(System.currentTimeMillis()));
            messageRepository.save(message1);
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
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
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            List<Map<String,Object>> personList = new ArrayList<>();
            //先根据userID，找到与这个人相关的收发信息
            //作为发送者发送的huo作为接收者收到的
            List<Message> mList = messageRepository.findAllBySenderIDOrReceiverIDAndType(userID,userID,3);
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
                List<Message> messageContactList = messageRepository.findAllBySenderIDAndReceiverIDOrReceiverIDAndSenderIDAndType(userID,contactID,userID,contactID,3);
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
                personMap.put("avatar",getUserAvatar(user.getImage()));
                personMap.put("isRead",isRead);
                personMap.put("messageList",messageList);
                personMap.put("userIdentity",user.getUserIdentity());
                personMap.put("authorID",user.getAuthorID());
                personList.add(personMap);
            }
            List<Message> systemMessageList = messageRepository.findAllByTypeNotAndReceiverID(3,userID);
            List<Map<String,Object>> messageList = new ArrayList<>();
            boolean isRead = true;
            for (Message sys : systemMessageList){
                if(!sys.getViewed()){
                    isRead = false;
                }
                Map<String,Object> tmp = new HashMap<>();
                tmp.put("message",sys.getContent());
                String strDateFormat = "yyyy-MM-dd HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
                tmp.put("time",sdf.format(sys.getSendTime()));
                messageList.add(tmp);
            }
            Map<String,Object> systemMessage = new HashMap<>();
            systemMessage.put("messageList",messageList);
            systemMessage.put("isRead",isRead);

            response.put("systemMessage",systemMessage);
            response.put("personList",personList);
            User me = userRepository.findByUserID(userID);
            response.put("avatar",getUserAvatar(me.getImage()));
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

    @PostMapping("/readMessage")
    public Map<String, Object> readMessage(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            String contactID = (String) req.get("personId");
            List<Message> messageContactList = new ArrayList<>();
            if(contactID == null){
                messageContactList = messageRepository.findAllByTypeNotAndReceiverID(3,userID);
            }
            else {
                messageContactList = messageRepository.findAllBySenderIDAndReceiverIDOrReceiverIDAndSenderID(userID,contactID,userID,contactID);
            }
            for(Message message : messageContactList){
                message.setViewed(true);
                messageRepository.save(message);
            }
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

}
