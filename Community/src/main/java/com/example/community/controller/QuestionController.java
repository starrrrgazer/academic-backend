package com.example.community.controller;

import com.example.community.dao.*;
import com.example.community.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
/*
* status = 1 表示获取成功，2表示失败
*
* */

@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443",
        "https://localhost:8000","https://localhost:80","https://localhost:443",
        "http://121.36.60.6:8000","http://121.36.60.6:80","http://121.36.60.6:443",
        "https://121.36.60.6:8000","https://121.36.60.6:80","https://121.36.60.6:443",
        "https://doorscholar.cn.","https://www.doorscholar.cn.","https://doorscholar.cn","https://doorscholar.cn"

},allowCredentials = "true",maxAge = 3600)
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

    public byte[] getUserAvatar(String image){
        try {
            File avatar = new File(image);
            if(avatar.exists() && avatar.canRead()) {
                byte[] buffer = new byte[(int) avatar.length()];
                InputStream in = new FileInputStream(avatar);
                in.read(buffer);
                return buffer;
            }
            else {
                File dft = new File("./static/image/default.jpg");
                if(dft.exists()) {
                    byte[] buffer = new byte[(int) dft.length()];
                    InputStream in = new FileInputStream(dft);
                    in.read(buffer);
                    return buffer;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("getUserImage error");
            return new byte[0];
        }
        return new byte[0];
    }

    public Map<String,Object> getUserByLogin(Map<String,Object> response){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);

        if(session.getAttribute("userID") == null){
            System.out.println("you donnt login");
            response.put("status",3);
        }
        else{
            String userID = (String) session.getAttribute("userID");
            System.out.println("now userId IS :" + userID);
            response.put("userID",userID);
        }
        return response;
    }

    @PostMapping("/getSocietyInformation")
    public Map<String, Object> getSocietyInformation(@RequestBody Map<String,Object> req){
        Map<String,Object> response = new HashMap<>();
        try {
            response.put("status",1);
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
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
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
        User user = userRepository.findByUserID(question.getUserID());
        Map<String,Object> questionMap = new HashMap<>();
        questionMap.put("proposerId",question.getUserID());
        questionMap.put("proposer",question.getUsername());
        questionMap.put("avatar", getUserAvatar(question.getAvatar()));
        questionMap.put("userIdentity",user.getUserIdentity());
        questionMap.put("authorID",user.getAuthorID());
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

    public Map<String,Object> putUserInfoToResponseMap(Map<String,Object> response, String userID){
        try {
            User user = userRepository.findByUserID(userID);
            response.put("avatar", getUserAvatar(user.getImage()));
            response.put("userName",user.getUsername());
            response.put("userIdentity",user.getUserIdentity());
            response.put("authorID",user.getAuthorID());
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
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }

    }

    public void checkResponseMap(Map<String,Object> response)throws Exception{
        if(response.containsKey("status") && (int)response.get("status") != 1){
            throw new Exception("status is " + response.get("status"));
        }
    }

    public List<Question> sortQuestionsByTime(List<Question> questions){
        Collections.sort(questions, new Comparator<Question>() {
            @Override
            public int compare(Question o1, Question o2) {
                Timestamp timestamp1 = o1.getQuestionTime();
                Timestamp timestamp2 = o2.getQuestionTime();
                return timestamp2.compareTo(timestamp1);
            }
        });
        return questions;
    }

    @PostMapping("/getRecommendedQuestions")
    public Map<String,Object> getRecommendedQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            response = putUserInfoToResponseMap(response,userID);
            checkResponseMap(response);

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
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);

            return response;
        }
    }

    @PostMapping("/getAskedQuestions")
    public Map<String,Object> getAskedQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            response = putUserInfoToResponseMap(response,userID);
            checkResponseMap(response);

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
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

    @PostMapping("/getAnsweredQuestions")
    public Map<String,Object> getAnsweredQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            response = putUserInfoToResponseMap(response,userID);
            checkResponseMap(response);

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
//            System.out.println("here2");
            if( response.containsKey("status") && (int)response.get("status") == 3){
//                System.out.println("here");
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

    @PostMapping("/getFollowQuestions")
    public Map<String,Object> getFollowQuestions(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try{
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            response = putUserInfoToResponseMap(response,userID);
            checkResponseMap(response);

            List<QuestionFollow> questionFollows = questionFollowRepository.findAllByUserID(userID);
            List<Question> questions = new ArrayList<>();
            for(QuestionFollow questionFollow : questionFollows){
                int questionID = questionFollow.getQuestionID();
                questions.add(questionRepository.findByQuestionID(questionID));
            }
            System.out.println(questions);
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
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
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
            int searchType = (int) req.get("searchType");
            int searchListType = (int) req.get("searchListType");
            String searchText = null;
            if(searchType == 9){
                searchText = (String) req.get("searchText");
                if(searchText == null){
                    throw new Exception("searchType is 9, but don't have searchText");
                }
            }
            else if(searchType > 9){
                searchType = searchType -1;
            }

            List<Question> questions = new ArrayList<>();

            if(searchListType == 1){
                questions = questionRepository.findAll();
            }
            else if (searchListType == 2){
                response = getUserByLogin(response);
                checkResponseMap(response);
                String userID = (String) response.get("userID");
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
//                searchInQuestionList(questions,searchType,searchText);
            }
            else if (searchListType == 3){
                response = getUserByLogin(response);
                checkResponseMap(response);
                String userID = (String) response.get("userID");
                questions = questionRepository.findAllByUserID(userID);
            }
            else if (searchListType == 4){
                response = getUserByLogin(response);
                checkResponseMap(response);
                String userID = (String) response.get("userID");
                List<QuestionAnswer> questionAnswers = questionAnswerRepository.findAllByUserID(userID);
                questions = new ArrayList<>();
                for(QuestionAnswer questionAnswer : questionAnswers){
                    int questionID = questionAnswer.getQuestionID();
                    questions.add(questionRepository.findByQuestionID(questionID));
                }
            }
            else if (searchListType == 5){
                response = getUserByLogin(response);
                checkResponseMap(response);
                String userID = (String) response.get("userID");
                List<QuestionFollow> questionFollows = questionFollowRepository.findAllByUserID(userID);
                questions = new ArrayList<>();
                for(QuestionFollow questionFollow : questionFollows){
                    int questionID = questionFollow.getQuestionID();
                    questions.add(questionRepository.findByQuestionID(questionID));
                }
            }
            else {
                throw new Exception("searchListType is not [1:5]");
            }
            questions = searchInQuestionList(questions,searchType,searchText);

            List<Map<String,Object>> questionList = new ArrayList<>();
            questions = sortQuestionsByTime(questions);
            for(Question question:questions){
                questionList.add(putQuestionMap(question,"userID"));
            }
            response.put("status",1);
            response.put("questionList",questionList);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

    public Question createQuestion(Map<String,Object> req)throws Exception{
        try {
            Map<String,Object> response = new HashMap<>();
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
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
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

    @Transactional
    @PostMapping("/editSkill")
    public Map<String, Object> editSkill(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            List<Integer> skillList = (List<Integer>) req.get("skillList");
            int deleteNum = userTagRepository.deleteAllByUserID(userID);
            System.out.println("deleteNum is : " + deleteNum);
            for (int tagsID : skillList){
                UserTags userTags = new UserTags();

                userTags.setTagsID(tagsID);
                userTags.setUserID(userID);
                userTagRepository.save(userTags);
            }

            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

    public Map<String,Object> putQuestionAnswerMap(QuestionAnswer questionAnswer){

        Map<String,Object> questionAnswerMap = new HashMap<>();
        User user = userRepository.findByUserID(questionAnswer.getUserID());
        questionAnswerMap.put("answerId",questionAnswer.getId());
        questionAnswerMap.put("answererId",questionAnswer.getUserID());
        questionAnswerMap.put("answerer",questionAnswer.getUsername());
        questionAnswerMap.put("avatar", getUserAvatar(questionAnswer.getAvatar()));
        questionAnswerMap.put("userIdentity",user.getUserIdentity());
        questionAnswerMap.put("authorID",user.getAuthorID());
        questionAnswerMap.put("answer",questionAnswer.getAnswerContent());
        String strDateFormat = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        questionAnswerMap.put("time",sdf.format(questionAnswer.getAnswerTime()));
        return questionAnswerMap;
    }

    public List<QuestionAnswer> sortQuestionsAnswerByTime(List<QuestionAnswer> questionAnswers){
        Collections.sort(questionAnswers, new Comparator<QuestionAnswer>() {
            @Override
            public int compare(QuestionAnswer o1, QuestionAnswer o2) {
                Timestamp timestamp1 = o1.getAnswerTime();
                Timestamp timestamp2 = o2.getAnswerTime();
                return timestamp2.compareTo(timestamp1);
            }
        });
        return questionAnswers;
    }
    @PostMapping("/getQuestionDetail")
    public Map<String, Object> getQuestionDetail(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            response = getUserByLogin(response);
//            checkResponseMap(response);
            int questionID = (int) req.get("questionId");
            Question question = questionRepository.findByQuestionID(questionID);
            response.putAll(putQuestionMap(question,question.getUserID()));
            List<Map<String,Object>> answerList = new ArrayList<>();
            List<QuestionAnswer> questionAnswers = questionAnswerRepository.findAllByQuestionID(questionID);
            questionAnswers = sortQuestionsAnswerByTime(questionAnswers);
            for(QuestionAnswer questionAnswer : questionAnswers){
                answerList.add(putQuestionAnswerMap(questionAnswer));
            }
            response.put("answerList",answerList);
            System.out.println(response);
            if(response.containsKey("status")){
                return response;
            }
            else {
                response.put("status",1);
            }
            return response;
        }catch (Exception e){
            e.printStackTrace();
            response.put("status",2);
            return response;
        }
    }

    public QuestionAnswer createQuestionAnswer(Map<String,Object> req)throws Exception{
        try {
            Map<String,Object> response = new HashMap<>();
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            int questionID = (int) req.get("questionId");
            String answerContent = (String) req.get("answer");

            User user = userRepository.findByUserID(userID);
            String avatar = user.getImage();
            String username = user.getUsername();

            QuestionAnswer questionAnswer = new QuestionAnswer();
            questionAnswer.setQuestionID(questionID);
            questionAnswer.setUserID(userID);
            questionAnswer.setAvatar(avatar);
            questionAnswer.setUsername(username);
            questionAnswer.setAnswerContent(answerContent);
            questionAnswer.setAnswerTime(new Timestamp(System.currentTimeMillis()));
            QuestionAnswer questionAnswer1 = questionAnswerRepository.save(questionAnswer);
            System.out.println(questionAnswer1.getQuestionID());
            return questionAnswer1;

        }catch (Exception e){
            throw new Exception("createQuestion error");
        }
    }

    @PostMapping("/answerQuestion")
    public Map<String, Object> answerQuestion(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {

            int questionID = (int) req.get("questionId");
            Question question = questionRepository.findByQuestionID(questionID);
            question.setAnswerAmount(question.getAnswerAmount()+1);
            QuestionAnswer questionAnswer = createQuestionAnswer(req);

            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

    @Transactional
    @PostMapping("/deleteAnswer")
    public Map<String, Object> deleteAnswer(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {

            int answerID = (int) req.get("answerId");
            QuestionAnswer questionAnswer = questionAnswerRepository.findById(answerID);
            Question question = questionRepository.findByQuestionID(questionAnswer.getQuestionID());
            question.setAnswerAmount(question.getAnswerAmount()-1);
            questionAnswerRepository.deleteById(answerID);
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

    @Transactional
    @PostMapping("/deleteQuestion")
    public Map<String, Object> deleteQuestion(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            int questionID = (int) req.get("questionId");
            questionRepository.deleteById(questionID);
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

    @PostMapping("/followQuestion")
    public Map<String, Object> followQuestion(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            int questionID = (int) req.get("questionId");
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            QuestionFollow q = questionFollowRepository.findByQuestionIDAndUserID(questionID,userID);
            if(q == null){
                QuestionFollow questionFollow = new QuestionFollow();
                questionFollow.setQuestionID(questionID);
                questionFollow.setUserID(userID);
                questionFollowRepository.save(questionFollow);
            }
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }


    @Transactional
    @PostMapping("/deleteFollowQuestion")
    public Map<String, Object> deleteFollowQuestion(@RequestBody Map<String,Object> req){
        System.out.println("request body is:" + req);
        Map<String,Object> response = new HashMap<>();
        try {
            int questionID = (int) req.get("questionId");
            response = getUserByLogin(response);
            checkResponseMap(response);
            String userID = (String) response.get("userID");
            QuestionFollow questionFollow = questionFollowRepository.findByQuestionIDAndUserID(questionID,userID);
            questionFollowRepository.deleteById(questionFollow.getId());
            response.put("status",1);
            return response;
        }catch (Exception e){
            e.printStackTrace();
            if( response.containsKey("status") && (int)response.get("status") == 3){
                return response;
            }
            response.put("status",2);
            return response;
        }
    }

}
