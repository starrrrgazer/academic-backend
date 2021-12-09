package com.example.community.util;

import com.example.community.entity.Question;
import com.example.community.entity.QuestionTag;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerParser {
    public static Map<String,Object> putQuestionMap(Question question, List<Integer> subjectList){
        Map<String,Object> questionMap = new HashMap<>();
        questionMap.put("proposerId",question.getUserID());
        questionMap.put("proposer",question.getUsername());
        questionMap.put("avatar",question.getAvatar());
        questionMap.put("questionId",question.getQuestionID());
        questionMap.put("questionTitle",question.getQuestionTitle());
        questionMap.put("questionInformation",question.getQuestionContent());
        questionMap.put("time",question.getQuestionTime());
        questionMap.put("answerAmount",question.getAnswerAmount());
        questionMap.put("subject",subjectList);
        return questionMap;
    }
}
