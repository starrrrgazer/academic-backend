package com.example.portal.entity;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Comment")
public class Comment {
    @Id
    @Column(name = "commentID")
    private String commentID;

    @Column(name = "content")
    private String content;

    @Column(name = "toID")
    private String toID;

    @Column(name = "userID")
    private String userID;

    @Column(name = "toType")
    private int toType;

    @Column(name = "commentTime")
    private DateTime commentTime;

    @Column(name = "commentTitle")
    private String commentTitle;

    public String getCommentTitle() {
        return commentTitle;
    }

    public void setCommentTitle(String commentTitle) {
        this.commentTitle = commentTitle;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getToType() {
        return toType;
    }

    public void setToType(int toType) {
        this.toType = toType;
    }

    public DateTime getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(DateTime commentTime) {
        this.commentTime = commentTime;
    }
}
