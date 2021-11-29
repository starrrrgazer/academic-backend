package com.example.community.controller;
// 接受前端请求去处理，用于前后端交互，第一层，之后调用服务层service实现后端的逻辑

import com.example.community.entity.demo;
import com.example.community.dao.DemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController//以json格式返回前端
public class CommunityController {
    @Autowired //编译时自动注入，
    private DemoRepository demoRepository;
    //json获取和返回用 java的map<>
    @GetMapping("/")
    public String hello(){
        return "hello";
    }
    @GetMapping("/test")
    public Map<String, Object> jpayNum(){
        demo a = demoRepository.findByNumber(1);
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("findByNumber",a);
        return map1;
    }
}
