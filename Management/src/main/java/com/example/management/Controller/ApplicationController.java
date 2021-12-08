package com.example.management.Controller;

import com.example.management.Service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/dealApplication")
    public Map<String,Object> dealApplication(Map<String,Object> map){
        return applicationService.dealApplicationConflict(map);
    }
}
