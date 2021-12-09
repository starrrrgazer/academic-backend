package com.example.community.entity;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Collect")
public class Collect {
    @Id
    @Column(name = "collectID")
    private int collectID;

    @Column(name = "userID")
    private String userID;

    @Column(name = "paperID")
    private String paperID;

    @Column(name = "collectTime")
    private DateTime collectTime;

    public int getCollectID() {
        return collectID;
    }

    public void setCollectID(int collectID) {
        this.collectID = collectID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPaperID() {
        return paperID;
    }

    public void setPaperID(String paperID) {
        this.paperID = paperID;
    }

    public DateTime getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(DateTime collectTime) {
        this.collectTime = collectTime;
    }
}
