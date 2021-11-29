package com.example.community.controller;
// 接受前端请求去处理，用于前后端交互，第一层，之后调用服务层service实现后端的逻辑

import com.example.community.dao.DemoRepository;
import com.example.community.dao.UserRepository;
import com.example.community.entity.User;
import com.example.community.entity.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController//以json格式返回前端
public class CommunityController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    DemoRepository demoRepository;
    //json获取和返回用 java的map<>
    @GetMapping("/")
    public String hello(){
        return "hello";
    }
    @GetMapping("/test1")
    public Map<String, Object> jpayNum(){
        int num = 1;
        demo a = demoRepository.findByTtest2(1);
        System.out.println(a);
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("findByTest1",a);
        return map1;
    }

    @GetMapping("/test")
    public Map<String, Object> getUserByID(){
        int num = 1;
//        if(userRepository)
        User user = userRepository.findByUserid(1);
        User user1 =userRepository.findByUsername("test1");
        System.out.println(user);
        System.out.println(user1);
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("findByID",user);
        map1.put("findByname",user1);
        return map1;
    }
}
