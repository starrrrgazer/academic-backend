package com.example.community.dao;

import com.example.community.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application,Object> {
    List<Application> findAllByUserID(String userID);
}
