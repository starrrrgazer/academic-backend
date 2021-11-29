package com.example.community.dao;
//数据层,通过xml对数据库进行一个具体的操作


import com.example.community.entity.demo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoRepository extends JpaRepository<demo, Long> {
    demo findByTest1(int test1);
}
