package com.example.community.controller;

import com.example.community.dao.UserRepository;
import com.example.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true",maxAge = 3600)
@RestController
public class LoginController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/changeUserIdentity")
    public Map<String,Object> changeUserIdentity(@RequestBody Map<String, Object> req){
        Map<String,Object> response = new HashMap<>();
        try {
            String userID = (String) req.get("userID");
            User user = userRepository.findByUserID(userID);
            if(user.getUserIdentity() == 1){
                response.put("status",300);
                return response;
            }
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
