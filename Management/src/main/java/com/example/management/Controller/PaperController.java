package com.example.management.Controller;

import com.example.management.Entity.Paper;
import com.example.management.Service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443"},allowCredentials = "true",maxAge = 3600)
@RestController
public class PaperController {

    @Autowired
    private PaperService paperService;


    @PostMapping("/addArticle")
    public Map<String,Object> addPaper(@RequestBody Map<String,Object> map){
        return paperService.addPaper(map);
    }

    @PostMapping("/deleteArticle")
    public Map<String,Object> deletePaper(@RequestBody Map<String,Object> map){
        return paperService.deletePaper(map);
    }

    @PostMapping("/getArticleList")
    public Map<String, Object> getArticleList(@RequestBody Map<String,Object> map){ return paperService.getArticleList(map);}
}
