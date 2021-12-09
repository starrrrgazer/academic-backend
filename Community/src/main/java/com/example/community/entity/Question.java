package com.example.community.entity;



import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "Question")
public class Question {
    @Id
    @Column(name = "questionID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionID;

    @Column(name = "referenceDocID")
    private String referenceDocID;

    @Column(name = "questionContent")
    private String questionContent;

    @Column(name = "userID")
    private String userID;

    @Column(name = "tagsID")
    private int tagsID;

    @Column(name = "questionTime")
    private Timestamp questionTime;

    @Column(name = "username")
    private String username;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "questionTitle")
    private String questionTitle;

    @Column(name = "answerAmount")
    private int answerAmount;

    public int getAnswerAmount() {
        return answerAmount;
    }

    public void setAnswerAmount(int answerAmount) {
        this.answerAmount = answerAmount;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public int getTagsID() {
        return tagsID;
    }

    public void setTagsID(int tagsID) {
        this.tagsID = tagsID;
    }

    public Timestamp getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(Timestamp questionTime) {
        this.questionTime = questionTime;
    }
}
