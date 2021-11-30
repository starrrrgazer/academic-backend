package com.example.community.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Report")
public class Report {
    @Id
    @Column(name = "reportID")
    private int reportID;

    @Column(name = "type")
    private int type;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private int status;

    @Column(name = "reporterID")
    private String reporterID;

    @Column(name = "reporteeID12")
    private String reporteeID12;

    @Column(name = "reporteeID34")
    private String reporteeID34;

    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReporterID() {
        return reporterID;
    }

    public void setReporterID(String reporterID) {
        this.reporterID = reporterID;
    }

    public String getReporteeID12() {
        return reporteeID12;
    }

    public void setReporteeID12(String reporteeID12) {
        this.reporteeID12 = reporteeID12;
    }

    public String getReporteeID34() {
        return reporteeID34;
    }

    public void setReporteeID34(String reporteeID34) {
        this.reporteeID34 = reporteeID34;
    }
}
