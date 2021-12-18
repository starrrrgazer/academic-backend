package com.example.management.mapper;


import com.example.management.Entity.Question;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface QuestionMapper {
    Question getQuestion(String id);
    void deleteQuestion(String id);
}
