package com.example.community.controller;

import com.example.community.dao.PaperRepository;
import com.example.community.dao.ReportRepository;
import com.example.community.entity.Paper;
import com.example.community.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/report")
    public Map<String, Object> report(@RequestBody Map<String,Object> req){
        Map<String,Object> response = new HashMap<>();
        try {
            int reporteeID = (int)req.get("reporteeID");
            int type = (int) req.get("type");
            String content = (String) req.get("content");
            Report report = new Report();
            report.setType(type);
            report.setReporteeID34(reporteeID);
            report.setReportTime(new Timestamp(System.currentTimeMillis()));
            reportRepository.save(report);
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }

    }

}
