package com.example.management.Entity;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.*;

@Entity
@Table(name = "Application")
public class Application {
    @Id
    @Column(name = "applicationID")
    private int applicationID;

    @Column(name = "userID")
    private String userID;

    @Column(name = "applicationTime")
    private DateTime applicationTime;

    @Column(name = "type")
    private int type;

    @Column(name = "emailAddress")
    private String emailAddress;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private int status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "title")
    private String title;

    @Column(name = "url")
    private String url;

    @Column(name = "phoneNumber1")
    private String phoneNumber1;

    @Column(name = "workCard1")
    private String workCard1;

    @Column(name = "phoneNumber2")
    private String phoneNumber2;

    @Column(name = "workCard2")
    private String workCard2;

    @Column(name = "authorID")
    private String authorID;

    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public DateTime getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(DateTime applicationTime) {
        this.applicationTime = applicationTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatusD() {
        return status;
    }

    public void setStatusD(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getWorkCard1() {
        return workCard1;
    }

    public void setWorkCard1(String workCard1) {
        this.workCard1 = workCard1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getWorkCard2() {
        return workCard2;
    }

    public void setWorkCard2(String workCard2) {
        this.workCard2 = workCard2;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }
}
