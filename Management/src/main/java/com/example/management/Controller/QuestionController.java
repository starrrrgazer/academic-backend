package com.example.management.Controller;


import com.example.management.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443",
        "https://localhost:8000","https://localhost:80","https://localhost:443",
        "http://121.36.60.6:8000","http://121.36.60.6:80","http://121.36.60.6:443",
        "https://121.36.60.6:8000","https://121.36.60.6:80","https://121.36.60.6:443"},allowCredentials = "true",maxAge = 3600)
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
