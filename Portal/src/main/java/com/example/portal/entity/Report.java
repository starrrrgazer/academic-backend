package com.example.portal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Report")
public class Report {
    @Id
    @Column(name = "reportID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportID;

    @Column(name = "type")
    private int type;

    @Column(name = "userID")
    private String userID;

    @Column(name = "reportTime")
    private Timestamp reportTime;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private int status;

    @Column(name = "reporterID")
    private String reporterID;

    @Column(name = "reporteeID")
    private String reporteeID;

    @Column(name = "reporteeID12")
    private String reporteeID12;

    @Column(name = "reporteeID34")
    private int reporteeID34;
}
