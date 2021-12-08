package com.example.portal.entity;

import javax.persistence.*;
//仅仅记录一下建表的过程
// 数据库建表
@Entity
@Table(name = "testCreate")
public class demo {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)//自动生成值,次次递增
//    private int id;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "test1")
    private int test1;


    @Column(name = "myTest")
    private int myTest;

    public Long getId() {
        return id;
    }

    public int getTest1() {
        return test1;
    }

    public int getMyTest() {
        return myTest;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTest1(int test1) {
        this.test1 = test1;
    }

    public void setMyTest(int myTest) {
        this.myTest = myTest;
    }
}
