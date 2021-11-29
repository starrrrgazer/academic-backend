package com.example.community.entity;

import javax.persistence.*;
//仅仅记录一下建表的过程
// 数据库建表
@Entity
@Table(name = "test")
public class demo {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)//自动生成值,次次递增
//    private int id;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "test1")
    private int test1;

    @Column(name = "third")
    private int third;

    @Column(name = "ttest2")
    private int ttest2;

    public int getTtest2() {
        return ttest2;
    }

    public void setTtest2(int ttest2) {
        this.ttest2 = ttest2;
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTest1() {
        return test1;
    }

    public void setTest1(int number) {
        this.test1 = number;
    }

}
