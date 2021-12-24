package com.example.community.dao;

import com.example.community.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report,Object> {
    List<Report> findAllByUserID(String userID);
}
