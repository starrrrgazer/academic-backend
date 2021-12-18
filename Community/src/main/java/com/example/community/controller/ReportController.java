package com.example.community.controller;

import com.example.community.dao.PaperRepository;
import com.example.community.dao.ReportRepository;
import com.example.community.entity.Paper;
import com.example.community.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true",maxAge = 3600)
@RestController
public class ReportController {
    @Autowired
    ReportRepository reportRepository;

    public void checkResponseMap(Map<String,Object> response)throws Exception{
        if(response.containsKey("status") && (int)response.get("status") != 1){
            throw new Exception("status is " + response.get("status"));
        }
    }
    public Map<String,Object> getUserByLogin(Map<String,Object> response){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);

        if(session.getAttribute("userID") == null){
            response.put("status",3);
        }
        else{
            String userID = (String) session.getAttribute("userID");
            response.put("userID",userID);
        }
        return response;
    }
    @PostMapping("/report")
    public Map<String, Object> report(@RequestBody Map<String,Object> req){
        Map<String,Object> response = new HashMap<>();
        try {
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            int reporteeID = (int)req.get("reporteeID");
            int type = (int) req.get("type");
            String content = (String) req.get("content");
            Report report = new Report();
            report.setUserID(userID);
            report.setType(type);
            if(type == 5 || type == 4)
                report.setReporteeID34(reporteeID);
            else {
                throw new Exception("type is not 4 or 5");
            }
            report.setReportTime(new Timestamp(System.currentTimeMillis()));
            reportRepository.save(report);
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }

    }

}
