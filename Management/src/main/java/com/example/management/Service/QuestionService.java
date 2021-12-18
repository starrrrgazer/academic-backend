package com.example.management.Service;

import com.example.management.Entity.Question;
import com.example.management.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    public Map<String,Object> getQuestion(Map<String, Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        Map<String, Object> question1 = new HashMap<>();
        try{
            String id = (String) map.get("id");
            Question question = questionMapper.getQuestion(id);
            question1.put("questionID", question.getQuestionID());
            question1.put("referenceDocID", question.getReferenceDocID());
            question1.put("userID", question.getUserID());
            question1.put("tagsID", question.getTagsID());
            question1.put("questionTime", question.getQuestionTime());
            question1.put("questionTitle", question.getQuestionTitle());
            question1.put("answerAmount", question.getAnswerAmount());
            question1.put("username", question.getUsername());
            question1.put("avatar", question.getAvatar());
        }catch (Exception e){
            e.printStackTrace();
            returnObject.put("status","401");
            return returnObject;
        }
        returnObject.put("question",question1);
        returnObject.put("status","200");
        return returnObject;
    }

    public Map<String,Object> deleteQuestion(Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        try{
            String id = (String) map.get("id");
            questionMapper.deleteQuestion(id);
        }catch (Exception e){
            e.printStackTrace();
            returnObject.put("status","401");
            return returnObject;
        }
        returnObject.put("status","200");
        return returnObject;
    }
}
