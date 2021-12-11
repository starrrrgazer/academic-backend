package com.example.management.Service;

import com.example.management.Entity.User;
import com.example.management.mapper.UserMapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
    @Autowired
    private RestHighLevelClient restHighLevelClient;

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
            Date unblockDate = new Date(sdf.parse((String) map.get("time")).getTime());
            userMapper.blockUser(userID, unblockDate, (String) map.get("kind"));
        }catch (Exception e){
            e.printStackTrace();
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

    public Map<String,Object> getResearcherList(Map<String,Object> map){
        String id = (String) map.get("id");
        Map<String,Object> returnObject = new HashMap<>();
        Map<String,Object> researcher = new HashMap<>();
        try {
            User user = userMapper.getUserByuserId(id);
            researcher.put("userID",user.getUserID());
            researcher.put("authorID",user.getAuthorID());
            researcher.put("userIdentity",user.getUserIdentity());
            researcher.put("username",user.getUsername());
            researcher.put("password",user.getPassword());
            researcher.put("phoneNumber",user.getPhoneNumber());
            researcher.put("emailAddress",user.getEmailAddress());
            researcher.put("image",user.getImage());
            researcher.put("organization",user.getOrganization());
            researcher.put("introduction",user.getIntroduction());
            researcher.put("realName",user.getRealName());
            researcher.put("userPosition",user.getUserPosition());
            researcher.put("isBanned",user.getIsBanned());
            researcher.put("unblockTime",user.getUnblockTime());
        } catch (Exception e) {
            returnObject.put("status","403");
            returnObject.put("result","未知错误");
        }
        returnObject.put("status","200");
        returnObject.put("result","成功");
        returnObject.put("researcherList",researcher);
        return returnObject;
    }
}
