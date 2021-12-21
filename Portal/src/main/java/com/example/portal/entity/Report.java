package com.example.portal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private int status;

    @Column(name = "reporterID")
    private String reporterID;

    @Column(name = "reporteeID")
    private String reporteeID;
}
