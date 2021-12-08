package com.example.management.Controller;

import com.example.management.Service.UserService;
import com.example.management.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/blockUser")
    public Map<String,Object> blockUser(Map<String,Object> map){
        return userService.blockUser(map);
    }

    @PostMapping("/unblockUser")
    public Map<String,Object> unblockUser(Map<String,Object> map){
        return userService.unblockUser(map);
    }
}
