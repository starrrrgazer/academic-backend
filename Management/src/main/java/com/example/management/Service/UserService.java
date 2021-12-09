package com.example.management.Service;

import com.example.management.Entity.User;
import com.example.management.mapper.UserMapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public Map<String,Object> blockUser(Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        try{//userid
            String userID = (String) map.get("id");
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
            String userID = (String) map.get("id");
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

    public Map<String,Object> getResearcherList(){
        //不会ES
        Map<String,Object> returnObject = new HashMap<>();
        List<Map<String,Object>> researcherList = new ArrayList<>();
        for (int i = 0;i < 5;i++){
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("id","123423");
            tmp.put("h_index",123414);
            tmp.put("n_citation",3323);
            tmp.put("n_pubs",23);
            tmp.put("name","aaa");
            tmp.put("position","bbb");
            Map<String,Object> sadsad= new HashMap<>();
            sadsad.put("i","2433");sadsad.put("r","sda");
            tmp.put("pubs",sadsad);
            tmp.put("pubsI","efqw");
            tmp.put("pubsR",22);
            Map<String,Object> sadsad1= new HashMap<>();
            sadsad.put("tagsT","2va");sadsad.put("tagsW","sda");
            tmp.put("tags",sadsad1);
        }
//        try{
//            List<User> tmpReseacherList = userMapper.getResearcherList();
//            for (User user : tmpReseacherList){
//                Map<String,Object> tmp = new HashMap<>();
//                tmp.put("id",user.getUserID());
//
//            }
//        } catch (Exception e) {
//            returnObject.put("status","401");
//            returnObject.put("result","未知错误");
//            return returnObject;
//        }

        returnObject.put("status","200");
        returnObject.put("result","成功");
        returnObject.put("researcherList",researcherList);
        return returnObject;
    }
}
