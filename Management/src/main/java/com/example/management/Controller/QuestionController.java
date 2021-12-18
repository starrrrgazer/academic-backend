package com.example.management.Controller;


import com.example.management.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @PostMapping("/getQuestion")
    Map<String,Object> getQuestion(@RequestBody Map<String,Object> map){
        return questionService.getQuestion(map);
    }

    @PostMapping("/deleteQuestion")
    Map<String,Object> deleteQuestion(@RequestBody Map<String,Object>map){
        return questionService.deleteQuestion(map);
    }
}
