package com.example.literature.controller;
// 接受前端请求去处理，用于前后端交互，第一层，之后调用服务层service实现后端的逻辑
import com.example.literature.dao.PaperRepository;
import com.example.literature.entity.AuthorList;
import com.example.literature.entity.Paper;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true",maxAge = 3600)
@RestController//以json格式返回前端
public class LiteratureController {
    @Autowired
    PaperRepository paperRepository;
    @Autowired
    RestHighLevelClient restHighLevelClient;


    @GetMapping("/searchPaper")
    public Map<String, Object> searchPaper(@RequestParam Map<String,Object> params){
        Map<String,Object> map = new HashMap<String,Object>();
        //map.put();
        int pageSize = (int) params.get("pageSize");
        int pageNum = (int) params.get("pageNum");
        String startdate_init = (String)params.get("startdate");
        int startdate = Integer.parseInt(startdate_init.substring(0,4));
        String enddate_init = (String)params.get("enddate");
        int enddate = Integer.parseInt(enddate_init.substring(0,4));
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> conditionlist = (List<Map<String,Object>>) params.get("conditionList");
        int conditionNum = conditionlist.size();
        List<Paper> searchedPaper = new ArrayList<Paper>();
        for (Map<String,Object> condition:conditionlist) {
            int type = (int) condition.get("type");
            String context = (String) condition.get("context");
            int relationship = (int) condition.get("relationship");
            boolean isCurrent = (boolean) condition.get("isCurrent");
            if(type == 0){

            }
            else if(type == 1){

            }
            else if(type == 2){

            }
            else if(type == 3){

            }
            else if(type == 4){

            }
            else if(type == 5){

            }
        }
        return map;
    }


    @GetMapping("/recommendPaper")
    public Map<String, Object> recommendPaper(@RequestParam Map<String,Object> params) {
        Map<String,Object> map = new HashMap<String,Object>();
        return map;
    }



    @GetMapping("/getPaperdetail")
    public Map<String, Object> getPaperdetail(@RequestParam Map<String,Object> params) {
        Map<String,Object> map = new HashMap<String,Object>();
        String id = (String) params.get("id");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("id", id));
        //增加查询条件
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            SearchHit searchHit = Hits[0];
            (String) searchHit.getSourceAsMap().get("title");

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        /*Paper paper_by_id = paperRepository.findById(id);
        map.put("title",paper_by_id.getTitle());
        map.put("citiation",paper_by_id.getN_citation());
        map.put("issn",paper_by_id.getIssn());
        map.put("doi",paper_by_id.getDoi());
        map.put("abstract",paper_by_id.getAbstracts());
        map.put("venue",paper_by_id.getVenues().getRaw());
        map.put("year",paper_by_id.getYear());
        map.put("pdf",paper_by_id.getPdf());
        ArrayList<Map<String,Object>> authorlist = new ArrayList();
        for (AuthorList author:paper_by_id.getAuthors()) {
            Map<String,Object> map1 = new HashMap<String,Object>();
            map1.put("authorName",author.getName());
            map1.put("authorId",author.getId());
            map1.put("authorOrg",author.getOrgs());
            authorlist.add(map1);
        }
        map.put("authors",authorlist);
        map.put("keyWords",paper_by_id.getKeywords());
        map.put("urls",paper_by_id.getId());*/
        return map;
    }


    @GetMapping("/classificationPaper")
    public Map<String, Object> classificationPaper(@RequestParam Map<String,Object> params) {
        Map<String,Object> map = new HashMap<String,Object>();

        return map;
    }
    @GetMapping("/test")
    public void test() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("title", "trans"));
        //增加查询条件
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            int i=0;
            for (SearchHit searchHit : Hits) {
                String name = (String) searchHit.getSourceAsMap().get("title");
                Integer birth = (Integer) searchHit.getSourceAsMap().get("year");
                String interest = (String) searchHit.getSourceAsMap().get("abstract");
                List<String> keywords = (List<String>) searchHit.getSourceAsMap().get("keywords");
                List<AuthorList> authors = (List<AuthorList>)searchHit.getSourceAsMap().get("authors");
                System.out.println("-------------" + (++i) + "------------");
                System.out.println(name);
                System.out.println(birth);
                System.out.println(interest);
                System.out.println(keywords);
                System.out.println(authors);
                if(i>5)
                    break;
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
