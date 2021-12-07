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

@RestController
public class LoginController {
//    @Autowired
//    HttpSession session;
    @Autowired
    UserRepository userRepository;

    @CrossOrigin(origins = "http://localhost:8080",allowCredentials = "true",maxAge = 3600)
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> loginMap) {

        System.out.println("someone try to login");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        if(requestAttributes != null){
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
//        }
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        session = request.getSession();
        System.out.println(session.getId());
        Map<String, Object> map = new HashMap<>();
        if(session.getAttribute("username") != null){
            map.put("message", "用户已登录！");
        }
        else{
            String username = (String) loginMap.get("username");
            String password = (String) loginMap.get("password");
            System.out.println(username);
            System.out.println(password);
            try {
                User user = userRepository.findByUsername(username);
//                System.out.println(user.getUserID());
                if (user != null) {
                    if(user.getPassword().equals(password)){
                        System.out.println(session.getAttribute("username"));
                        session.setAttribute("username", username);
                        session.setAttribute("password", password);
                        System.out.println(session.getAttribute("username"));

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

    @CrossOrigin(origins = "http://localhost:8080",allowCredentials = "true",maxAge = 3600)
    @PostMapping("/show_info")
    public Map<String, Object> showInfo() {
        System.out.println("showinfo");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        if(requestAttributes != null){
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
