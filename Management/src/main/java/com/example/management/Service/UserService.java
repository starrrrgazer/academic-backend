package com.example.management.Service;

import com.example.management.Entity.User;
import com.example.management.mapper.UserMapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Map<String,Object> blockUser(Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        try{//userid
            String userID = (String) map.get("userID");
            User a = userMapper.getUserByuserId(userID);
            if (a == null) {
                returnObject.put("status", "403");
                returnObject.put("result", "用户不存在");
                return returnObject;
            }
            //unblockDate
            String df = "yyyy-MM-dd HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(df);
            Date unblockDate = Date.valueOf(sdf.format(map.get("unblockDate")));
            userMapper.blockUser(userID, unblockDate);
        }catch (Exception e){
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }
        returnObject.put("status","200");
        returnObject.put("result","封禁成功");
        return returnObject;
    }

    public Map<String,Object> unblockUser(Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();

        try{
            String userID = (String) map.get("userID");
            User a = userMapper.getUserByuserId(userID);
            if (a == null) {
                returnObject.put("status", "403");
                returnObject.put("result", "用户不存在");
                return returnObject;
            }
            String df = "yyyy-MM-dd HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(df);
            Date date = Date.valueOf(sdf.format(new java.util.Date()));
            userMapper.unblockUser(userID,date);
        } catch (Exception e) {
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }
        returnObject.put("status","200");
        returnObject.put("result","解封成功");
        return returnObject;
    }
}
