package com.example.management.Controller;

import com.example.management.Service.UserService;
import com.example.management.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/block")
    public Map<String,Object> blockUser(@RequestBody Map<String,Object> map){
        return userService.blockUser(map);
    }

    @PostMapping("/unblock")
    public Map<String,Object> unblockUser(@RequestBody Map<String,Object> map){
        return userService.unblockUser(map);
    }

    @PostMapping("/getResearcherList")
    public Map<String,Object> getResearcherList(@RequestBody Map<String,Object> map){
        return userService.getResearcherList(map);
    }

    @PostMapping("/deleteAuthorID")
    public Map<String,Object> deleteAuthorID(@RequestBody Map<String,Object> map){
        return userService.deleteAuthorID(map);
    }

    @PostMapping("/resetAuthorID")
    public Map<String,Object> resetAuthorID(@RequestBody Map<String,Object> map){
        return userService.resetAuthorID(map);
    }
    @PostMapping("/getAuthor")
    public Map<String,Object>getAuthor(@RequestBody Map<String,Object> map){
        return userService.getAuthor(map);
    }
    @PostMapping("/getOrganization")
    public Map<String,Object> getOrganization(@RequestBody Map<String,Object> map){
        return userService.getOrganization(map);
    }
}
