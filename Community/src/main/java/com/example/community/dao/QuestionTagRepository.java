package com.example.community.dao;

import com.example.community.entity.QuestionTag;
import org.hibernate.boot.JaccPermissionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag,Object> {
    List<QuestionTag> findAllByQuestionID(int questionID);
}
