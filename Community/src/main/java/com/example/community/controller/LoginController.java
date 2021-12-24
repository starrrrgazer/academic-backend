package com.example.community.controller;

import com.example.community.dao.*;
import com.example.community.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443",
        "https://localhost:8000","https://localhost:80","https://localhost:443",
        "http://121.36.60.6:8000","http://121.36.60.6:80","http://121.36.60.6:443",
        "https://121.36.60.6:8000","https://121.36.60.6:80","https://121.36.60.6:443",
        "https://doorscholar.cn.","https://www.doorscholar.cn.","https://doorscholar.cn","https://doorscholar.cn"

},allowCredentials = "true",maxAge = 3600)
@RestController
public class LoginController {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserTagRepository userTagRepository;
    @Autowired
    QuestionTagRepository questionTagRepository;
    @Autowired
    QuestionAnswerRepository questionAnswerRepository;
    @Autowired
    QuestionFollowRepository questionFollowRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ReportRepository reportRepository;

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

    @Transactional
    @PostMapping("/cancelAccount")
    public Map<String,Object> cancelAccount(@RequestBody Map<String,Object> req){
        Map<String,Object> response = new HashMap<>();
        try {
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            User user = userRepository.findByUserID(userID);
            String authorID = user.getAuthorID();
            List<Application> applicationList = applicationRepository.findAllByUserID(userID);
            for (Application application : applicationList){
                applicationRepository.delete(application);
            }
            List<Comment> commentList = commentRepository.findAllByUserID(userID);
            for(Comment comment : commentList){
                commentRepository.delete(comment);
            }
            List<Message> messageList = messageRepository.findAllBySenderIDOrReceiverID(userID,userID);
            for(Message message : messageList){
                messageRepository.delete(message);
            }
            List<Question> questionList = questionRepository.findAllByUserID(userID);
            for(Question question : questionList){
                questionRepository.delete(question);
            }
            List<QuestionAnswer> questionAnswerList = questionAnswerRepository.findAllByUserID(userID);
            for (QuestionAnswer questionAnswer : questionAnswerList){
                questionAnswerRepository.delete(questionAnswer);
            }
            List<QuestionFollow> questionFollowList = questionFollowRepository.findAllByUserID(userID);
            for (QuestionFollow questionFollow : questionFollowList){
                questionFollowRepository.delete(questionFollow);
            }
            List<Report> reportList = reportRepository.findAllByUserID(userID);
            for (Report report : reportList){
                reportRepository.delete(report);
            }
            List<UserTags> userTagsList = userTagRepository.findAllByUserID(userID);
            for (UserTags userTags : userTagsList){
                userTagRepository.delete(userTags);
            }
            userRepository.delete(user);
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

    @PostMapping("/changeUserIdentity")
    public Map<String,Object> changeUserIdentity(@RequestBody Map<String, Object> req){
        Map<String,Object> response = new HashMap<>();
        try {
            String userID = (String) req.get("userID");
            User user = userRepository.findByUserID(userID);
//            if(user.getUserIdentity() == 1){
//                response.put("status",300);
//                return response;
//            }
            user.setUserIdentity(3);
            userRepository.save(user);
            response.put("status",200);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",301);
            return response;
        }
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> loginMap) {

        System.out.println("someone try to login");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        System.out.println(session.getId());
        Map<String, Object> map = new HashMap<>();
        if(session.getAttribute("userID") != null){
            map.put("message", "用户已登录！");
        }
        else{
            String username = (String) loginMap.get("username");
            String password = (String) loginMap.get("password");
            try {
                User user = userRepository.findByUsername(username);
                if (user != null) {
                    if(user.getPassword().equals(password)){
                        session.setAttribute("userID", user.getUserID());
                        session.setAttribute("username", username);
                        session.setAttribute("password", password);
                        session.setAttribute("isBanned",user.getIsBanned());
                        map.put("success", true);
                        map.put("message", "用户登录成功！");
                    }
                    else {
                        map.put("success", false);
                        map.put("message", "密码错误！");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                map.put("success", false);
                map.put("message", "用户登录失败！");
            }
        }
        return map;
    }


    @PostMapping("/show_info")
    public Map<String, Object> showInfo() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        Map<String, Object> map = new HashMap<>();
        if(session.getAttribute("username")
                == null){
            map.put("message", "用户未登录！");
        }
        else{
            String username = (String) session.getAttribute("username");
            String password = (String) session.getAttribute("password");
            System.out.println("username is" + username);
            System.out.println("password is" + password);
            map.put("username", username);
            map.put("password", password);
        }
        return map;
    }
}
