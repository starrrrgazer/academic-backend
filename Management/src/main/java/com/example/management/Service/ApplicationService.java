package com.example.management.Service;

import com.example.management.Entity.Application;
import com.example.management.Entity.User;
import com.example.management.mapper.ApplicationMapper;
import com.example.management.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private UserMapper userMapper;

    public Map<String,Object> dealApplicationConflict(Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        try{
            String authorId = (String) map.get("authorId");
            User a = userMapper.getUserByAuthorID(authorId);
            Application application = applicationMapper.getApplicationByAuthorID(authorId);
            if (a==null){
                returnObject.put("status","403");
                returnObject.put("result","门户不存在");
                return returnObject;
            }
            if (application==null){
                returnObject.put("status","403");
                returnObject.put("result","申请不存在");
                return returnObject;
            }
            //处理结果
            String dealResult = (String) map.get("dealResult");
            a.setUserID(dealResult);
        } catch (Exception e) {
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }
        returnObject.put("status","200");
        returnObject.put("result","处理成功");
        return returnObject;
    }
}
