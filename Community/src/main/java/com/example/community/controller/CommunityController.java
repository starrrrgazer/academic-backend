package com.example.community.controller;
// 接受前端请求去处理，用于前后端交互，第一层，之后调用服务层service实现后端的逻辑

import com.example.community.dao.DemoRepository;
import com.example.community.dao.PaperRepository;
import com.example.community.dao.UserRepository;
import com.example.community.entity.Paper;
import com.example.community.entity.User;
import com.example.community.entity.demo;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController//以json格式返回前端
public class CommunityController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    DemoRepository demoRepository;
    //json获取和返回用 java的map<>
    @GetMapping("/")
    public String hello(){
        return "hello";
    }
    @GetMapping("/test1")
    public Map<String, Object> jpayNum(){
        int num = 1;
        demo a = demoRepository.findByMyTest(1);
        System.out.println(a);
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("findByTest1",a);
        return map1;
    }

    @GetMapping("/test")
    public Map<String, Object> getUserByID(){
        int num = 1;
//        if(userRepository)
        User user = userRepository.findByUserID("1");
        User user1 =userRepository.findByUsername("test1");
        System.out.println(user);
        System.out.println(user1);
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("findByID",user);
        map1.put("findByname",user1);
        return map1;
    }

    @Autowired
    PaperRepository paperRepository;
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @GetMapping("/ES")
    public void testESConnect(){
//        List<Paper> paperList = paperRepository.findByAbstractsLike("wll");
//        System.out.println("run here");
//        if(paperList.isEmpty()){
//            System.out.println("LIST is empty");
//        }
//        for(Paper paper : paperList){
//            System.out.println(paper);
//        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("title", "wll"));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            long totalhits = searchHits.getTotalHits().value;
            System.out.println(totalhits);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }


}
