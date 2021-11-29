package com.example.community.entity;

import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)//主键生成策略
    @Column(name = "userid", nullable = false)
    private int userid;

    @Column(name = "authorid")
    private String authorid;

    @Column(name = "useridentity")
    private int useridentity;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "phonenumber")
    private String phonenumber;

    @Column(name = "emailaddress")
    private String emailaddress;

    @Column(name = "image")
    private String image;

    @Column(name = "organization")
    private String organization;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "realname")
    private String realname;

    @Column(name = "userposition")
    private String userposition;

    @Column(name = "isbanned")
    private int isbanned;

    @Column(name = "unblocktime")
    private DateTime unblocktime;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public int getUseridentity() {
        return useridentity;
    }

    public void setUseridentity(int useridentity) {
        this.useridentity = useridentity;
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

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmailaddress() {
        return emailaddress;
    }

    public void setEmailaddress(String emailaddress) {
        this.emailaddress = emailaddress;
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

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getUserposition() {
        return userposition;
    }

    public void setUserposition(String userposition) {
        this.userposition = userposition;
    }

    public int getIsbanned() {
        return isbanned;
    }

    public void setIsbanned(int isbanned) {
        this.isbanned = isbanned;
    }

    public DateTime getUnblocktime() {
        return unblocktime;
    }

    public void setUnblocktime(DateTime unblocktime) {
        this.unblocktime = unblocktime;
    }


}
