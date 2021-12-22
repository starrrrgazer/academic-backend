package com.example.management.Service;

import com.example.management.Entity.Report;
import com.example.management.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private ReportMapper reportMapper;

    public Map<String,Object> getCommentList(){
        Map<String,Object> returnObject = new HashMap<>();
        List<Map<String,Object>> reportList = new ArrayList<>();
        try {
            List<Report> tmpReportList = reportMapper.getReportList();

            for (Report report : tmpReportList){
                Map<String,Object> tmp = new HashMap<>();
                tmp.put("reportID",report.getReportID());
                tmp.put("type",report.getType());
                tmp.put("content",report.getContent());
                tmp.put("status",report.getStatus());
                tmp.put("processerID",report.getProcesserID());
                tmp.put("reporteeID12",report.getReporteeID12());
                tmp.put("reporteeID34",report.getReporteeID34());
                tmp.put("reportTime",report.getReportTime());
                tmp.put("userID",report.getUserID());
                tmp.put("result",report.getResult());
                reportList.add(tmp);
            }
        }catch (Exception e) {
            e.printStackTrace();
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }

        returnObject.put("status","200");
        returnObject.put( "result","成功");
        returnObject.put("reportList",reportList);
        return returnObject;
    }


    public Map<String,Object> acceptReport(Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        try {
            String reportId = (String) map.get("reportId");
            String result = (String) map.get("result");
            Report r = reportMapper.getReportById(reportId);
            if (r==null){
                returnObject.put("status","403");
                returnObject.put("result","举报不存在");
                return returnObject;
            }
            reportMapper.acceptReport(reportId,result);
        }catch (Exception e) {
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }

        returnObject.put("status","200");
        returnObject.put( "result","成功");
        return returnObject;
    }

    public Map<String,Object> rejectReport(Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        try {
            String reportId = (String) map.get("reportId");
            String result = (String) map.get("result");
            Report r = reportMapper.getReportById(reportId);
            if (r==null){
                returnObject.put("status","403");
                returnObject.put("result","举报不存在");
                return returnObject;
            }
            reportMapper.rejectReport(reportId,result);
        }catch (Exception e) {
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }

        returnObject.put("status","200");
        returnObject.put( "result","成功");
        return returnObject;
    }

}
