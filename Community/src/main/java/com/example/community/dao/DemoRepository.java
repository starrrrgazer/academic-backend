package com.example.community.dao;

import com.example.community.entity.demo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoRepository extends JpaRepository<demo,Object> {
    demo findByMyTest(int num);

}
