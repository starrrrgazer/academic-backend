package com.example.management.Service;

import com.example.management.Entity.Application;
import com.example.management.Entity.User;
import com.example.management.mapper.ApplicationMapper;
import com.example.management.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public Map<String,Object> getApplyList(){
        Map<String,Object> returnObject = new HashMap<>();
        List<Map<String,Object>> applications = new ArrayList<>();
        try {
            List<Application> tmpApplications = applicationMapper.getApplyList();

            for (Application tmpApplication : tmpApplications){
                Map<String,Object> tmp = new HashMap<>();
                tmp.put("applicationID",tmpApplication.getApplicationID());
                tmp.put("userID",tmpApplication.getUserID());
                tmp.put("applicationTime",tmpApplication.getApplicationTime());
                tmp.put("type",tmpApplication.getType());
                tmp.put("emailAddress",tmpApplication.getEmailAddress());
                tmp.put("content",tmpApplication.getContent());
                tmp.put("status",tmpApplication.getStatusD());
                tmp.put("reason",tmpApplication.getReason());
                tmp.put("title",tmpApplication.getTitle());
                tmp.put("url",tmpApplication.getUrl());
                tmp.put("phoneNumber1",tmpApplication.getPhoneNumber1());
                tmp.put("workCard1",tmpApplication.getWorkCard1());
                tmp.put("authorId",tmpApplication.getAuthorID());
                tmp.put("phoneNumber2",tmpApplication.getPhoneNumber2());
                tmp.put("workCard2",tmpApplication.getWorkCard2());
                applications.add(tmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }
        returnObject.put("status","200");
        returnObject.put("result","处理成功");
        returnObject.put("ApplyList",applications);
        return returnObject;
    }

    public Map<String,Object> acceptApply(@RequestBody Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        try {
            String applyId = (String) map.get("applyId");
            Application a = applicationMapper.getApplicationByID(applyId);
            if (a==null){
                returnObject.put("status","403");
                returnObject.put("result","申请不存在");
                return returnObject;
            }
            applicationMapper.acceptApply(applyId);
        } catch (Exception e) {
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }
        returnObject.put("status","200");
        returnObject.put("result","成功");
        return returnObject;
    }


    public Map<String,Object> rejectApply(@RequestBody Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        try {
            String applyId = (String) map.get("applyId");
            Application a = applicationMapper.getApplicationByID(applyId);
            if (a==null){
                returnObject.put("status","403");
                returnObject.put("result","申请不存在");
                return returnObject;
            }
            applicationMapper.rejectApply(applyId);
        } catch (Exception e) {
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }
        returnObject.put("status","200");
        returnObject.put("result","成功");
        return returnObject;
    }
}
