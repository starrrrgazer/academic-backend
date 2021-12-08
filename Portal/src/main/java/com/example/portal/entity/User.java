package com.example.portal.entity;

import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import java.util.Date;
import java.util.UUID;

public class User {
    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(String userPosition) {
        this.userPosition = userPosition;
    }

    public int getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(int isBanned) {
        this.isBanned = isBanned;
    }

    public Date getUnblockTime() {
        return unblockTime;
    }

    public void setUnblockTime(Date unblockTime) {
        this.unblockTime = unblockTime;
    }

    @Id
    @Column(name = "userID")
    private UUID userID;

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public void setUserIdentity(int userIdentity) {
        this.userIdentity = userIdentity;
    }

    @Column(name = "authorID")
    private String authorID;

    @Column(name = "userIdentity")
    private int userIdentity;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "emailAddress")
    private String emailAddress;

    @Column(name = "image")
    private String image;

    @Column(name = "organization")
    private String organization;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "realName")
    private String realName;

    @Column(name = "userPosition")
    private String userPosition;

    @Column(name = "isBanned", nullable = false)
    private int isBanned;

    public int getUserIdentity() {
        return userIdentity;
    }

    @Column(name = "unblockTime")
    private Date unblockTime;

}
