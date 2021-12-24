package com.example.management.Controller;

import com.example.management.Service.UserService;
import com.example.management.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443",
        "https://localhost:8000","https://localhost:80","https://localhost:443",
        "http://121.36.60.6:8000","http://121.36.60.6:80","http://121.36.60.6:443",
        "https://121.36.60.6:8000","https://121.36.60.6:80","https://121.36.60.6:443",
        "https://doorscholar.cn.","https://www.doorscholar.cn.","https://doorscholar.cn","https://doorscholar.cn"

},allowCredentials = "true",maxAge = 3600)
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
