package com.example.management.Controller;

import com.example.management.Entity.Paper;
import com.example.management.Service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443",
        "https://localhost:8000","https://localhost:80","https://localhost:443",
        "http://121.36.60.6:8000","http://121.36.60.6:80","http://121.36.60.6:443",
        "https://121.36.60.6:8000","https://121.36.60.6:80","https://121.36.60.6:443",
        "https://doorscholar.cn.","https://www.doorscholar.cn.","https://doorscholar.cn","https://doorscholar.cn"

},allowCredentials = "true",maxAge = 3600)
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
