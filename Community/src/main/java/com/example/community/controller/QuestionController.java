package com.example.community.controller;

import com.example.community.dao.*;
import com.example.community.entity.*;
import com.example.community.util.ControllerParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class QuestionController {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserTagRepository userTagRepository;
    @Autowired
    QuestionTagRepository questionTagRepository;
    @Autowired
    QuestionAnswerRepository questionAnswerRepository;
    @Autowired
    QuestionFollowRepository questionFollowRepository;
    @Autowired
    UserRepository userRepository;
    @PostMapping("/getSocietyInformation")
    public Map<String, Object> getSocietyInformation(@RequestBody Map<String,Object> req){
        Map<String,Object> response = new HashMap<>();
        try {
            response.putAll(getAnsweredQuestions(req));
            checkResponseMap(response);
            response.putAll(getAskedQuestions(req));
            checkResponseMap(response);
            response.putAll(getFollowQuestions(req));
            checkResponseMap(response);
            response.putAll(getRecommendedQuestions(req));
            checkResponseMap(response);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",400);
            return response;
        }

    }


    public Map<String,Object> putQuestionMap(Question question){
        List<QuestionTag> questionTags = questionTagRepository.findAllByQuestionID(question.getQuestionID());
        List<Integer> subjectList = new ArrayList<>();
        for(QuestionTag questionTag : questionTags){
            subjectList.add(questionTag.getTagsID());
        }
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
    public Map<String,Object> putResponseMap(Map<String,Object> response,Map<String,Object> req){
        try {
            String userID = (String) req.get("userID");
            User user = userRepository.findByUserID(userID);
            response.put("avatar",user.getImage());
            response.put("userName",user.getUsername());
            List<UserTags> userTags = userTagRepository.findAllByUserID(userID);
            List<Integer> skillList = new ArrayList<>();
            for(UserTags userTag : userTags){
                skillList.add(userTag.getTagsID());
            }
            response.put("skillList",skillList);
            response.put("status",200);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",400);
            return response;
        }

    }

    public void checkResponseMap(Map<String,Object> response)throws Exception{
        if((int)response.get("status") != 200){
            throw new Exception("status is not 200");
        }
    }

    @PostMapping("/getRecommendedQuestions")
    public Map<String,Object> getRecommendedQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = putResponseMap(response,req);
            checkResponseMap(response);
            String userID = (String) req.get("userID");
            List<UserTags> userTags = userTagRepository.findAllByUserID(userID);
            System.out.println("usertags are : " + userTags);
            List<Question> questions = new ArrayList<>();
            for(UserTags userTag : userTags){
                int tagsID = userTag.getTagsID();
                questions.addAll(questionRepository.findAllByTagsID(tagsID));
            }
            List<Map<String,Object>> recommendedQuestionList = new ArrayList<>();
            for(Question question:questions){
                recommendedQuestionList.add(putQuestionMap(question));
            }
            response.put("status",200);
            response.put("recommendedQuestionList",recommendedQuestionList);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",400);
            return response;
        }
    }

    @PostMapping("/getAskedQuestions")
    public Map<String,Object> getAskedQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = putResponseMap(response,req);
            checkResponseMap(response);
            String userID = (String) req.get("userID");
            List<Question> questions = questionRepository.findAllByUserID(userID);
            List<Map<String,Object>> askedQuestionList = new ArrayList<>();
            for(Question question:questions){
                askedQuestionList.add(putQuestionMap(question));
            }
            response.put("status",200);
            response.put("askedQuestionList",askedQuestionList);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",400);
            return response;
        }
    }

    @PostMapping("/getAnsweredQuestions")
    public Map<String,Object> getAnsweredQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = putResponseMap(response,req);
            checkResponseMap(response);
            String userID = (String) req.get("userID");
            List<QuestionAnswer> questionAnswers = questionAnswerRepository.findAllByUserID(userID);
            List<Question> questions = new ArrayList<>();
            for(QuestionAnswer questionAnswer : questionAnswers){
                int questionID = questionAnswer.getQuestionID();
                questions.add(questionRepository.findByQuestionID(questionID));
            }
            List<Map<String,Object>> answeredQuestionList = new ArrayList<>();
            for(Question question:questions){
                answeredQuestionList.add(putQuestionMap(question));
            }
            response.put("status",200);
            response.put("answeredQuestionList",answeredQuestionList);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",400);
            return response;
        }
    }

    @PostMapping("/getFollowQuestions")
    public Map<String,Object> getFollowQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = putResponseMap(response,req);
            checkResponseMap(response);
            String userID = (String) req.get("userID");
            List<QuestionFollow> questionFollows = questionFollowRepository.findAllByUserID(userID);
            List<Question> questions = new ArrayList<>();
            for(QuestionFollow questionFollow : questionFollows){
                int questionID = questionFollow.getQuestionID();
                questions.add(questionRepository.findByQuestionID(questionID));
            }
            List<Map<String,Object>> followQuestionList = new ArrayList<>();
            for(Question question:questions){
                followQuestionList.add(putQuestionMap(question));
            }
            response.put("status",200);
            response.put("followQuestionList",followQuestionList);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",400);
            return response;
        }
    }

}
