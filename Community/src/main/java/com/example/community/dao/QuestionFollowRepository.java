package com.example.community.dao;

import com.example.community.entity.QuestionFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionFollowRepository extends JpaRepository<QuestionFollow,Object> {
    List<QuestionFollow> findAllByUserID(String userID);
}
