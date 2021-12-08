package com.example.management.mapper;

import com.example.management.Entity.Report;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportMapper {
    List<Report> getReportList();
    void acceptReport(String reportId,String result);
    void rejectReport(String reportId,String result);
    Report getReportById(String reportId);
}
