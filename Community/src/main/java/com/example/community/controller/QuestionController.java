package com.example.community.controller;

import com.example.community.dao.*;
import com.example.community.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
/*
* status = 1 表示获取成功，2表示失败
*
* */

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
            response.put("status",2);
            return response;
        }

    }

    public Boolean checkQuestionFollowed(int questionID,String userID){
        QuestionFollow questionFollow = questionFollowRepository.findByQuestionIDAndUserID(questionID,userID);
        try {
            int id = questionFollow.getId();
            return true;
        }catch (NullPointerException e){
            return false;
        }
    }

    public Map<String,Object> putQuestionMap(Question question, String userID){
//        System.out.println("question :" + question);
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
        String strDateFormat = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        questionMap.put("time",sdf.format(question.getQuestionTime()));
        questionMap.put("answerAmount",question.getAnswerAmount());
        questionMap.put("followed",checkQuestionFollowed(question.getQuestionID(),userID));
        questionMap.put("subject",subjectList);
        return questionMap;
    }

    /*这个函数是往返回的json里面加除了问题列表以外的有关用户信息的数据，如username*/
    public Map<String,Object> putUserInfoToResponseMap(Map<String,Object> response, Map<String,Object> req){
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
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }

    }

    public void checkResponseMap(Map<String,Object> response)throws Exception{
        if((int)response.get("status") != 200){
            throw new Exception("status is not 200");
        }
    }

    public List<Question> sortQuestionsByTime(List<Question> questions){
        Collections.sort(questions, new Comparator<Question>() {
            @Override
            public int compare(Question o1, Question o2) {
                Timestamp timestamp1 = o1.getQuestionTime();
                Timestamp timestamp2 = o2.getQuestionTime();
                if(timestamp1.after(timestamp2)){
                    return 0;
                }
                else {
                    return 1;
                }
            }
        });
        return questions;
    }

//    @Autowired
//    private HttpSession session;
//    public String getUserIDFromSession(){
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        session = request.getSession();
//
//        if(session.getAttribute("userID") == null){
//            System.out.println("用户未登录！");
//            return null;
//        }
//        else{
//            String username = (String) session.getAttribute("username");
//            String password = (String) session.getAttribute("password");
//            String userID = (String) session.getAttribute("userID");
//            return userID;
//        }
//    }

    @PostMapping("/getRecommendedQuestions")
    public Map<String,Object> getRecommendedQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = putUserInfoToResponseMap(response,req);
            checkResponseMap(response);
            String userID = (String) req.get("userID");
            List<UserTags> userTags = userTagRepository.findAllByUserID(userID);
            List<Question> questions = new ArrayList<>();
            for(UserTags userTag : userTags){
                int tagsID = userTag.getTagsID();
                List<QuestionTag> questionTags = questionTagRepository.findAllByTagsID(tagsID);
                for(QuestionTag questionTag : questionTags) {
                    Question question = questionRepository.findByQuestionIDAndUserIDNot(questionTag.getQuestionID(),userID);
                    if(question != null && !questions.contains(question))
                        questions.add(question);
                }
            }
            List<Map<String,Object>> recommendedQuestionList = new ArrayList<>();
            questions = sortQuestionsByTime(questions);
            for(Question question:questions){
                recommendedQuestionList.add(putQuestionMap(question,userID));
            }
            response.put("status",1);
            response.put("recommendedQuestionList",recommendedQuestionList);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }
    }

    @PostMapping("/getAskedQuestions")
    public Map<String,Object> getAskedQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = putUserInfoToResponseMap(response,req);
            checkResponseMap(response);
            String userID = (String) req.get("userID");
            List<Question> questions = questionRepository.findAllByUserID(userID);
            List<Map<String,Object>> askedQuestionList = new ArrayList<>();
            questions = sortQuestionsByTime(questions);
            for(Question question:questions){
                askedQuestionList.add(putQuestionMap(question,userID));
            }
            response.put("status",1);
            response.put("askedQuestionList",askedQuestionList);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }
    }

    @PostMapping("/getAnsweredQuestions")
    public Map<String,Object> getAnsweredQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = putUserInfoToResponseMap(response,req);
            checkResponseMap(response);
            String userID = (String) req.get("userID");
            List<QuestionAnswer> questionAnswers = questionAnswerRepository.findAllByUserID(userID);
            List<Question> questions = new ArrayList<>();
            for(QuestionAnswer questionAnswer : questionAnswers){
                int questionID = questionAnswer.getQuestionID();
                questions.add(questionRepository.findByQuestionID(questionID));
            }
            List<Map<String,Object>> answeredQuestionList = new ArrayList<>();
            questions = sortQuestionsByTime(questions);
            for(Question question:questions){
                answeredQuestionList.add(putQuestionMap(question,userID));
            }
            response.put("status",1);
            response.put("answeredQuestionList",answeredQuestionList);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }
    }

    @PostMapping("/getFollowQuestions")
    public Map<String,Object> getFollowQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = putUserInfoToResponseMap(response,req);
            checkResponseMap(response);
            String userID = (String) req.get("userID");
            List<QuestionFollow> questionFollows = questionFollowRepository.findAllByUserID(userID);
            List<Question> questions = new ArrayList<>();
            for(QuestionFollow questionFollow : questionFollows){
                int questionID = questionFollow.getQuestionID();
                questions.add(questionRepository.findByQuestionID(questionID));
            }
            List<Map<String,Object>> followQuestionList = new ArrayList<>();
            questions = sortQuestionsByTime(questions);
            for(Question question:questions){
                followQuestionList.add(putQuestionMap(question,userID));
            }
            response.put("status",1);
            response.put("followQuestionList",followQuestionList);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }
    }

    public List<Question> searchInQuestionList(List<Question> questions,int searchType, String searchText)throws Exception{
        try {
            Iterator<Question> iterator = questions.iterator();
            if(searchType != 9){
                while (iterator.hasNext()){
                    Question question = iterator.next();
                    List<QuestionTag> questionTags = questionTagRepository.findAllByQuestionID(question.getQuestionID());
                    boolean flag = false;
                    for(QuestionTag questionTag : questionTags){
                        if(questionTag.getTagsID() == searchType){
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        iterator.remove();
                    }
                }
            }
            else {
                while (iterator.hasNext()){
                    Question question = iterator.next();
                    if(question.getQuestionTitle().contains(searchText) || question.getQuestionContent().contains(searchText)){

                    }
                    else {
                        iterator.remove();
                    }
                }
            }
            return questions;
        }catch (Exception e){
            throw new Exception("search error");
        }

    }

    @PostMapping("/searchQuestion")
    public Map<String,Object> searchQuestion(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            /*
            * searchType,1-8为对应问题类型搜索，9为关键词搜索
            * searchText,如果为关键词搜索，此处为关键词
            * searchListType,Int,是在哪个列表进行搜索，1为allQuestionList，2为recommendedQuestionList，3为askedQuestionList，4为answeredQuestionList，5为followQuestionList
            * */
            String userID = (String) req.get("userID");
            int searchType = (int) req.get("searchType");
            int searchListType = (int) req.get("searchListType");
            String searchText = null;
            if(searchType == 9){
                searchText = (String) req.get("searchText");
                if(searchText == null){
                    throw new Exception("searchType is 9, but don't have searchText");
                }
            }

            List<Question> questions = new ArrayList<>();

            if(searchListType == 1){
                questions = questionRepository.findAll();
            }
            else if (searchListType == 2){
                List<UserTags> userTags = userTagRepository.findAllByUserID(userID);
                for(UserTags userTag : userTags){
                    int tagsID = userTag.getTagsID();
                    List<QuestionTag> questionTags = questionTagRepository.findAllByTagsID(tagsID);
                    for(QuestionTag questionTag : questionTags) {
                        Question question = questionRepository.findByQuestionIDAndUserIDNot(questionTag.getQuestionID(),userID);
                        if(question != null && !questions.contains(question))
                            questions.add(question);
                    }
                }
                questions = sortQuestionsByTime(questions);
                searchInQuestionList(questions,searchType,searchText);
            }
            else if (searchListType == 3){
                questions = questionRepository.findAllByUserID(userID);
            }
            else if (searchListType == 4){
                List<QuestionAnswer> questionAnswers = questionAnswerRepository.findAllByUserID(userID);
                questions = new ArrayList<>();
                for(QuestionAnswer questionAnswer : questionAnswers){
                    int questionID = questionAnswer.getQuestionID();
                    questions.add(questionRepository.findByQuestionID(questionID));
                }
            }
            else if (searchListType == 5){
                List<QuestionFollow> questionFollows = questionFollowRepository.findAllByUserID(userID);
                questions = new ArrayList<>();
                for(QuestionFollow questionFollow : questionFollows){
                    int questionID = questionFollow.getQuestionID();
                    questions.add(questionRepository.findByQuestionID(questionID));
                }
            }
            else {
                throw new Exception("searchType is not [1:5]");
            }
            questions = searchInQuestionList(questions,searchType,searchText);

            List<Map<String,Object>> questionList = new ArrayList<>();
            questions = sortQuestionsByTime(questions);
            for(Question question:questions){
                questionList.add(putQuestionMap(question,userID));
            }
            response.put("status",1);
            response.put("questionList",questionList);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }
    }

    public Question createQuestion(Map<String,Object> req)throws Exception{
        try {
            String userID = (String) req.get("userID");
            String questionTitle = (String) req.get("questionTitle");
            String questionInformation = (String) req.get("questionInformation");

            User user = userRepository.findByUserID(userID);
            String avatar = user.getImage();
            String username = user.getUsername();

            Question question = new Question();
            question.setUserID(userID);
            question.setAvatar(avatar);
            question.setUsername(username);
            question.setQuestionTitle(questionTitle);
            question.setQuestionContent(questionInformation);
            question.setQuestionTime(new Timestamp(System.currentTimeMillis()));
            Question question1 = questionRepository.save(question);
            System.out.println(question1.getQuestionID());
            return question1;

        }catch (Exception e){
            throw new Exception("createQuestion error");
        }
    }

    public void createQuestionTag(List<Integer> subject, int questionID)throws Exception{
        try {
            for(int tagsID : subject){
                QuestionTag questionTag = new QuestionTag();
                questionTag.setQuestionID(questionID);
                questionTag.setTagsID(tagsID);
                questionTagRepository.saveAndFlush(questionTag);
            }
        }catch (Exception e){
            throw new Exception("createQuestionTag error");
        }
    }


    @PostMapping("/addQuestion")
    public Map<String, Object> addQuestion(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            Question question = createQuestion(req);

            List<Integer> subject = (List<Integer>) req.get("subject");
            System.out.println(question.getQuestionID());
            createQuestionTag(subject,question.getQuestionID());

            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }
    }



}
