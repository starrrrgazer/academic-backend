package com.example.community.entity;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Question")
public class Question {
    @Id
    @Column(name = "questionID")
    private int questionID;

    @Column(name = "referenceDocID")
    private String referenceDocID;

    @Column(name = "questionContent")
    private String questionContent;

    @Column(name = "userID")
    private String userID;

    @Column(name = "tags")
    private String tags;

    @Column(name = "questionTime")
    private DateTime questionTime;

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getReferenceDocID() {
        return referenceDocID;
    }

    public void setReferenceDocID(String referenceDocID) {
        this.referenceDocID = referenceDocID;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public DateTime getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(DateTime questionTime) {
        this.questionTime = questionTime;
    }
}
