package com.example.community.entity;

import javax.persistence.*;
//仅仅记录一下建表的过程
// 数据库建表
@Entity
@Table(name = "testCreate")
public class demo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自动生成值,次次递增
    private int id;

    @Column(name = "test1")
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
