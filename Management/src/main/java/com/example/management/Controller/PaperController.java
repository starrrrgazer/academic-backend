package com.example.management.Controller;

import com.example.management.Entity.AuthorList;
import com.example.management.Entity.Venue;
import com.example.management.Service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class PaperController {

    @Autowired
    private PaperService paperService;


    @PostMapping("/addPaper")
    public Map<String,Object> addPaper(Map<String,Object> map){
        return paperService.addPaper(map);
    }

    @PostMapping("/deletePaper")
    public Map<String,Object> deletePaper(Map<String,Object> map){
        return paperService.deletePaper(map);
    }

    @PostMapping("/getArticleList")
    public Map<String,Object> getArticleList(Map<String,Object> map){ return paperService.getArticleList(map);}
}
