package com.example.management.Controller;

import com.example.management.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443"},allowCredentials = "true",maxAge = 3600)
@RestController
public class ReportController {
    @Autowired
    private ReportService reportService;

    @PostMapping("/getReportList")
    public Map<String,Object> getReportList(){
        return reportService.getCommentList();
    }

    @PostMapping("acceptReport")
    public Map<String,Object> acceptReport(@RequestBody Map<String,Object> map){
        return reportService.acceptReport(map);
    }

    @PostMapping("rejectReport")
    public Map<String,Object> rejectReport(@RequestBody Map<String,Object> map){
        return reportService.rejectReport(map);
    }
}
