package com.example.management.Controller;

import com.example.management.Service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443",
        "http://121.36.60.6:8000","http://121.36.60.6:80","http://121.36.60.6:443",
        "https://121.36.60.6:8000","https://121.36.60.6:80","https://121.36.60.6:443"},allowCredentials = "true",maxAge = 3600)
@RestController
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/dealApplication")
    public Map<String,Object> dealApplication(@RequestBody Map<String,Object> map){
        return applicationService.dealApplicationConflict(map);
    }

    @PostMapping("/getApplyList")
    public Map<String,Object> getApplyList(){
        return applicationService.getApplyList();
    }

    @PostMapping("/acceptApply")
    public Map<String,Object> acceptApply(@RequestBody Map<String,Object> map){
        return applicationService.acceptApply(map);
    }

    @PostMapping("/rejectApply")
    public Map<String,Object> rejectApply(@RequestBody Map<String,Object> map){
        return applicationService.rejectApply(map);
    }

}
