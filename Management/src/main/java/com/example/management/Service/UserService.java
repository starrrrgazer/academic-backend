package com.example.management.Service;

import com.example.management.Entity.User;
import com.example.management.mapper.UserMapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public Map<String,Object> blockUser(Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        try{//userid
            String userID = (String) map.get("id");
            User a = userMapper.getUserByuserId(userID);
            if (a == null) {
                returnObject.put("status", "403");
                returnObject.put("result", "用户不存在");
                return returnObject;
            }
            //unblockDate
            String df = "yyyy-MM-dd HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(df);
            Date unblockDate = Date.valueOf(sdf.format(map.get("unblockDate")));
            userMapper.blockUser(userID, unblockDate);
        }catch (Exception e){
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }
        returnObject.put("status","200");
        returnObject.put("result","封禁成功");
        return returnObject;
    }

    public Map<String,Object> unblockUser(Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();

        try{
            String userID = (String) map.get("id");
            User a = userMapper.getUserByuserId(userID);
            if (a == null) {
                returnObject.put("status", "403");
                returnObject.put("result", "用户不存在");
                return returnObject;
            }
            String df = "yyyy-MM-dd HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(df);
            Date date = Date.valueOf(sdf.format(new java.util.Date()));
            userMapper.unblockUser(userID,date);
        } catch (Exception e) {
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }
        returnObject.put("status","200");
        returnObject.put("result","解封成功");
        return returnObject;
    }

    public Map<String,Object> getResearcherList(Map<String,Object> map){
      String id = (String) map.get("id");
        Map<String,Object> returnObject = new HashMap<>();
        List<Map<String,Object>> researcherList = new ArrayList<>();
       BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        SearchRequest searchRequest = new SearchRequest("author");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("id", id));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            SearchHit hit = Hits[0];
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("id",id);
            tmp.put("h_index", hit.getSourceAsMap().get("h_index"));
            List<Map<String, Object>> list = (List<Map<String, Object>>) hit.getSourceAsMap().get("pubs");
            tmp.put("pubs",list);
            List<Map<String, Object>> list1 = (List<Map<String, Object>>) hit.getSourceAsMap().get("tags");
            tmp.put("tags",list1);
            tmp.put("n_citation", hit.getSourceAsMap().get("n_citation"));
            tmp.put("n_pubs", hit.getSourceAsMap().get("n_pubs"));
            tmp.put("name", hit.getSourceAsMap().get("name"));
            tmp.put("position",hit.getSourceAsMap().get("position"));
            researcherList.add(tmp);
        } catch (Exception e) {
            returnObject.put("status","403");
            returnObject.put("result","未知错误");
        }
        returnObject.put("status","200");
        returnObject.put("result","成功");
        returnObject.put("researcherList",researcherList);
        return returnObject;
    }
}
