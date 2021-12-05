package com.example.community.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "UserTags")
public class UserTags {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "userID")
    private String userID;

    @Column(name = "tagsID")
    private int tagsID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
