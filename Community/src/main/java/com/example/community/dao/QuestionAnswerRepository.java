package com.example.community.dao;

import com.example.community.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer,Object> {
    List<QuestionAnswer> findAllByUserID(String userID);
}
