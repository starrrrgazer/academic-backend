package com.example.portal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Application")
public class Application {
    @Id
    @Column(name = "applicationID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int applicationID;

    @Column(name = "userID")
    private String userID;

    @Column(name = "applicationTime")
    private Date applicationTime;

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
}
