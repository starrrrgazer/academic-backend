package com.example.community.dao;

import com.example.community.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Object> {
    List<Question> findAll();
    List<Question> findAllByTagsID(int tagsID);
    List<Question> findAllByUserID(String userID);
    Question findByQuestionID(int questionID);
}
