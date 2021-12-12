package com.example.portal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "User")
public class User {
    @Id
    @Column(name = "userID")
    private String userID;

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

    @Column(name = "unblockTime")
    private Date unblockTime;

}
