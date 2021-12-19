package com.example.literature.controller;
// 接受前端请求去处理，用于前后端交互，第一层，之后调用服务层service实现后端的逻辑
import com.example.literature.dao.CommentRepository;
import com.example.literature.dao.PaperRepository;
import com.example.literature.dao.ReportRepository;
import com.example.literature.dao.UserRepository;
import com.example.literature.entity.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true",maxAge = 3600)
@RestController//以json格式返回前端
public class LiteratureController {
    @Autowired
    PaperRepository paperRepository;
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReportRepository reportRepository;

    @PostMapping("/searchPaper")
    public Map<String, Object> searchPaper(@RequestBody Map<String, Object> params) {
        System.out.println(params);
        Map<String, Object> map = new HashMap<String, Object>();
        int pageSize = (int) params.get("pageSize");
        int pageNum = (int) params.get("pageNum");
        boolean f =true;
        ArrayList date = null;
        if(params.get("date").getClass()==String.class)
            f =false;
        else
            date = (ArrayList) params.get("date");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> conditionlist = (List<Map<String, Object>>) params.get("conditionList");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        for (Map<String, Object> condition : conditionlist) {
            int type = (int) condition.get("type");
            String context = (String) condition.get("context");
            int relationship = (int) condition.get("relationship");
            Boolean isCurrent ;
            if((int) condition.get("isCurrent")==1){
                isCurrent =true;
            }
            else {
                isCurrent = false;
            }

            if (type == 0) {//全部检索
                if (isCurrent) {//精确
                    boolQueryBuilder.should(QueryBuilders.termQuery("title", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("abstracts", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("keywords", context));
                } else {//模糊
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("title", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("abstracts", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("keywords", context));
                }

            } else if (type == 1) {//题目检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("title", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("title", context));

                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("title", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("title", context));
                    }
                }
            } else if (type == 2) {//作者检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                }
            } else if (type == 3) {//作者单位检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                }
            } else if (type == 4) {//关键词检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                }
            } else if (type == 5) {//摘要检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                }
            }
        }
        if(f&&date.size()!=0)
        boolQueryBuilder.must(QueryBuilders.rangeQuery("year").from(date.get(0)).to(date.get(1)));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            int num = Hits.length;
            int page_number;
            if (num > 0) {
                int flag = num % pageSize;
                if (flag == 0) {
                    page_number = num / pageSize;
                } else {
                    page_number = num / pageSize + 1;
                }
                map.put("status", 200);
                map.put("paperNum", num);
                map.put("pageAllNum", page_number);
                List<Map<String, Object>> paperList = new ArrayList<Map<String, Object>>();
                List<String> yearSortList = new ArrayList<String>();
                List<String> languageSortList = new ArrayList<String>();
                List<String> authorSortList = new ArrayList<String>();
                List<String> organizationSortList = new ArrayList<String>();
                languageSortList.add("English("+num+")");
                Map<Integer,Integer> yearmap = new HashMap<>();
                Map<String,Integer> authormap = new HashMap<>();
                Map<String,Integer> orgnizationmap = new HashMap<>();
                for (SearchHit hit:Hits) {
                    if(!yearmap.containsKey((Integer) hit.getSourceAsMap().get("year"))){
                        yearmap.put((Integer)hit.getSourceAsMap().get("year"), 1);
                    }
                    else
                        yearmap.put((Integer)hit.getSourceAsMap().get("year"), yearmap.get((Integer) hit.getSourceAsMap().get("year"))+1);
                    List<Map<String,Object>> authorlist = (List<Map<String,Object>>)hit.getSourceAsMap().get("authors");
                    for (Map<String,Object>author:authorlist) {
                        if(!authormap.containsKey((String) author.get("name"))){
                            authormap.put((String) author.get("name"),1);
                        }
                        else
                            authormap.put((String) author.get("name"), authormap.get((String) author.get("name"))+1);
                    }
                    for (Map<String,Object>author:authorlist) {
                        if(!orgnizationmap.containsKey((String) author.get("org"))){
                            orgnizationmap.put((String) author.get("org"),1);
                        }
                        else
                            orgnizationmap.put((String) author.get("org"), orgnizationmap.get((String) author.get("org"))+1);
                    }
                }
                authormap.remove(null);
                orgnizationmap.remove(null);
                int i=0;
                for(Integer year:yearmap.keySet()){
                    yearSortList.add(year+"("+yearmap.get(year)+")");
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>10) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>5) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>3) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    authorSortList.add(name + "(" + authormap.get(name) + ")");
                    i++;
                }
                i=0;
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>100) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>30) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>10) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                    i++;
                }
                int control = (pageNum-1) * pageSize;
                while (control < pageNum * pageSize && control<num) {
                    SearchHit hit = Hits[control++];
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("title", hit.getSourceAsMap().get("title"));
                    String str = (String) hit.getSourceAsMap().get("abstract");
                    if (str.length()>200)
                        map1.put("abstract", str.substring(0, 200) + "....");
                    else
                        map1.put("abstract", str);
                    List<String> keyWords = (List<String>) hit.getSourceAsMap().get("keywords");
                    if(keyWords.size()>10){
                        map1.put("keyWords", keyWords.subList(0,10));
                    }
                    else
                        map1.put("keyWords", hit.getSourceAsMap().get("keywords"));
                    List<Map<String, Object>> list = (List<Map<String, Object>>) hit.getSourceAsMap().get("authors");
                    List<Map<String, Object>> list1 = new ArrayList<>();
                    if (list.size() >= 3) {
                        list1 = list.subList(0, 3);
                    }
                    map1.put("author", list1);
                    map1.put("venue", hit.getSourceAsMap().get("venue"));
                    map1.put("id", hit.getSourceAsMap().get("id"));
                    map1.put("year", hit.getSourceAsMap().get("year"));
                    paperList.add(map1);
                }
                map.put("paperList", paperList);
                map.put("yearSortList", yearSortList);
                map.put("languageSortList", languageSortList);
                map.put("authorSortList", authorSortList);
                map.put("organizationSortList", organizationSortList);
                System.out.println(123);
                System.out.println(map.get("paperList"));
                System.out.println(456);
            } else {
                map.put("status", 441);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return map;
    }


    @PostMapping("/recommendPaper")
    public Map<String, Object> recommendPaper(@RequestBody Map<String, Object> params) {
        int type = (int)params.get("type");
        Map<String, Object> map = new HashMap<String, Object>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if(type == 1)
        boolQueryBuilder.must(QueryBuilders.termQuery("year", 2020));
        else{
            boolQueryBuilder.must(QueryBuilders.rangeQuery("n_citation")
                    .gte(10000)
                    .lte(100000));
        }
        //增加查询条件
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(300);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            int i = 0;
            int num1 = (int) (Math.random() * 100);
            int num2 = (int) (Math.random() * 100) + 100;
            int num3 = (int) (Math.random() * 100) + 200;
            SearchHit hit1 = Hits[num1];
            SearchHit hit2 = Hits[num2];
            SearchHit hit3 = Hits[num3];
            map.put("status", 200);
            List<Map<String, Object>> paperList = new ArrayList<>();
            Map<String, Object> map1 = new HashMap<>();
            Map<String, Object> map2 = new HashMap<>();
            Map<String, Object> map3 = new HashMap<>();
            map1.put("title", hit1.getSourceAsMap().get("title"));
            String str1 = (String) hit1.getSourceAsMap().get("abstract");
            if (str1.length()>200)
                map1.put("abstract", str1.substring(0, 200) + "....");
            else
                map1.put("abstract", str1);
            map1.put("id", hit1.getSourceAsMap().get("id"));
            map2.put("title", hit2.getSourceAsMap().get("title"));
            String str2 = (String) hit2.getSourceAsMap().get("abstract");
            if (str2.length()>200)
                map2.put("abstract", str2.substring(0, 200) + "....");
            else
                map2.put("abstract", str2);
            map2.put("id", hit2.getSourceAsMap().get("id"));
            map3.put("title", hit3.getSourceAsMap().get("title"));
            String str3 = (String) hit3.getSourceAsMap().get("abstract");
            if (str3.length()>200)
                map3.put("abstract", str3.substring(0, 200) + "....");
            else
                map3.put("abstract", str3);
            map3.put("id", hit3.getSourceAsMap().get("id"));
            paperList.add(map1);
            paperList.add(map2);
            paperList.add(map3);
            map.put("paperList", paperList);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        System.out.println(map.get("paperList"));
        return map;
    }


    @PostMapping("/getPaperdetail")
    public Map<String, Object> getPaperdetail(@RequestBody Map<String, Object> params) {
        System.out.println(params);
        Map<String, Object> map = new HashMap<String, Object>();
        String id = (String) params.get("id");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("id", id));
        //增加查询条件
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            if (Hits.length > 0) {
                SearchHit searchHit = Hits[0];
                map.put("language", "english");
                map.put("title", searchHit.getSourceAsMap().get("title"));
                map.put("citation", searchHit.getSourceAsMap().get("n_citation"));
                map.put("issn", searchHit.getSourceAsMap().get("issn"));
                map.put("doi", searchHit.getSourceAsMap().get("doi"));
                map.put("abstract", searchHit.getSourceAsMap().get("abstract"));
                map.put("venue", searchHit.getSourceAsMap().get("venue"));
                map.put("year", searchHit.getSourceAsMap().get("year"));
                map.put("pdf", searchHit.getSourceAsMap().get("pdf"));
                map.put("authors", searchHit.getSourceAsMap().get("authors"));
                map.put("keyWords", searchHit.getSourceAsMap().get("keywords"));
                map.put("urls", searchHit.getSourceAsMap().get("url"));

                List<Map<String,Object>> authors = (List<Map<String,Object>>)searchHit.getSourceAsMap().get("authors");
                String org = (String) authors.get(0).get("org");
                //TODO 获取对应的相关学者
                System.out.println(org);

                List<Map<String,Object>> related_authors= new ArrayList<>();

                BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
                //建空查询
                SearchRequest searchRequest1 = new SearchRequest("author");
                SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
                boolQueryBuilder1.should(QueryBuilders.fuzzyQuery("org", "school"));
                //增加查询条件
                searchSourceBuilder1.query(boolQueryBuilder1);
                searchSourceBuilder1.size(1000);
                searchRequest1.source(searchSourceBuilder1);
                SearchResponse searchResponse1 = restHighLevelClient.search(searchRequest1, RequestOptions.DEFAULT);
                SearchHits searchHits1 = searchResponse1.getHits();
                SearchHit[] Hits1 = searchHits1.getHits();
                System.out.println(Hits1.length);
                int i = 0;
                for(SearchHit hit :Hits1){
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("id",hit.getSourceAsMap().get("id"));
                    map1.put("name",hit.getSourceAsMap().get("name"));
                    map1.put("org",hit.getSourceAsMap().get("org"));
                    related_authors.add(map1);
                    if(i++>5){
                        break;
                    }
                }
                map.put("relevantSchoolars",related_authors);

                List<Comment> commentList = commentRepository.findAllByToID(id);
                List<Map<String,Object>> list = new ArrayList<>();
                for (Comment com:commentList) {
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("context",com.getContent());
                    map1.put("time",com.getCommentTime());
                    map1.put("id",com.getCommentID());
                    map1.put("name",(userRepository.findByUserID(com.getUserID())).getUsername());
                    list.add(map1);
                }
                map.put("commentList",list);
                map.put("id",id);
                map.put("status", 200);
                System.out.println(map.get("relevantSchoolars"));
                System.out.println(map.get("citation"));
            } else
                map.put("status", 441);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return map;
    }

    @PostMapping("/classificationPaper")
    public Map<String, Object> classificationPaper(@RequestBody Map<String, Object> params) {
        System.out.println(params);
        Map<String, Object> map = new HashMap<String, Object>();
        boolean f =true;
        ArrayList date = null;
        if(params.get("date").getClass()==String.class)
            f =false;
        else
            date = (ArrayList) params.get("date");
        int pageSize = (int) params.get("pageSize");
        int pageNum = (int) params.get("pageNum");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> conditionlist = (List<Map<String, Object>>) params.get("conditionList");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        for (Map<String, Object> condition : conditionlist) {
            int type = (int) condition.get("type");
            String context = (String) condition.get("context");
            int relationship = (int) condition.get("relationship");
            Boolean isCurrent ;
            if((int) condition.get("isCurrent")==1){
                isCurrent =true;
            }
            else {
                isCurrent = false;
            }
            if (type == 0) {//全部检索
                if (isCurrent) {//精确
                    boolQueryBuilder.should(QueryBuilders.termQuery("title", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("abstracts", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("keywords", context));
                } else {//模糊
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("title", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("abstracts", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("keywords", context));
                }

            } else if (type == 1) {//题目检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("title", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("title", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("title", context));
                    }
                }
            } else if (type == 2) {//作者检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                }
            } else if (type == 3) {//作者单位检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                }
            } else if (type == 4) {//关键词检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                }
            } else if (type == 5) {//摘要检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                }
            } else if (type == 6) {//年份检索
                if (relationship == 1) {//与
                    boolQueryBuilder.must(QueryBuilders.termQuery("year", context));

                } else if (relationship == 2) {//或
                    boolQueryBuilder.should(QueryBuilders.termQuery("year", context));
                } else if (relationship == 3) {//非
                    boolQueryBuilder.mustNot(QueryBuilders.termQuery("year", context));
                }
            } else if (type == 7) {

            } else if (type == 8) {//作者检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                }
            } else if (type == 9) {//作者机构检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                }
            } else if (type == 10) {//作者检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                }
            } else if (type == 11) {//第一作者检索

            } else if (type == 12) {//作者机构检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                }
            }
        }
        if(f&&date.size()!=0)
            boolQueryBuilder.must(QueryBuilders.rangeQuery("year").from(date.get(0)).to(date.get(1)));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            int num = Hits.length;
            int page_number;
            if (num > 0) {
                int flag = num % pageSize;
                if (flag == 0) {
                    page_number = num / pageSize;
                } else {
                    page_number = num / pageSize + 1;
                }
                map.put("status", 200);
                map.put("paperNum", num);
                map.put("pageAllNum", page_number);
                List<Map<String, Object>> paperList = new ArrayList<Map<String, Object>>();
                List<String> yearSortList = new ArrayList<String>();
                List<String> languageSortList = new ArrayList<String>();
                List<String> authorSortList = new ArrayList<String>();
                List<String> organizationSortList = new ArrayList<String>();
                languageSortList.add("English("+num+")");
                Map<Integer,Integer> yearmap = new HashMap<>();
                Map<String,Integer> authormap = new HashMap<>();
                Map<String,Integer> orgnizationmap = new HashMap<>();
                for (SearchHit hit:Hits) {
                    if(!yearmap.containsKey((Integer) hit.getSourceAsMap().get("year"))){
                        yearmap.put((Integer)hit.getSourceAsMap().get("year"), 1);
                    }
                    else
                        yearmap.put((Integer)hit.getSourceAsMap().get("year"), yearmap.get((Integer) hit.getSourceAsMap().get("year"))+1);
                    List<Map<String,Object>> authorlist = (List<Map<String,Object>>)hit.getSourceAsMap().get("authors");
                    for (Map<String,Object>author:authorlist) {
                        if(!authormap.containsKey((String) author.get("name"))){
                            authormap.put((String) author.get("name"),1);
                        }
                        else
                            authormap.put((String) author.get("name"), authormap.get((String) author.get("name"))+1);
                    }
                    for (Map<String,Object>author:authorlist) {
                        if(!orgnizationmap.containsKey((String) author.get("org"))){
                            orgnizationmap.put((String) author.get("org"),1);
                        }
                        else
                            orgnizationmap.put((String) author.get("org"), orgnizationmap.get((String) author.get("org"))+1);
                    }
                }
                authormap.remove(null);
                orgnizationmap.remove(null);
                int i=0;
                for(Integer year:yearmap.keySet()){
                    yearSortList.add(year+"("+yearmap.get(year)+")");
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>10) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>5) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>3) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    authorSortList.add(name + "(" + authormap.get(name) + ")");
                    i++;
                }
                i=0;
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>100) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>30) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>10) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                    i++;
                }
                int control = (pageNum-1) * pageSize;
                while (control < pageNum * pageSize && control<num) {
                    SearchHit hit = Hits[control++];
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("title", hit.getSourceAsMap().get("title"));
                    String str = (String) hit.getSourceAsMap().get("abstract");
                    if (str.length()>200)
                        map1.put("abstract", str.substring(0, 200) + "....");
                    else
                        map1.put("abstract", str);
                    List<String> keyWords = (List<String>) hit.getSourceAsMap().get("keywords");
                    if(keyWords.size()>10){
                        map1.put("keyWords", keyWords.subList(0,10));
                    }
                    else
                        map1.put("keyWords", hit.getSourceAsMap().get("keywords"));
                    List<Map<String, Object>> list = (List<Map<String, Object>>) hit.getSourceAsMap().get("authors");
                    List<Map<String, Object>> list1 = new ArrayList<>();
                    if (list.size() >= 3) {
                        list1 = list.subList(0, 3);
                    }
                    map1.put("author", list1);
                    map1.put("venue", hit.getSourceAsMap().get("venue"));
                    map1.put("id", hit.getSourceAsMap().get("id"));
                    map1.put("year", hit.getSourceAsMap().get("year"));
                    paperList.add(map1);
                }
                map.put("paperList", paperList);
                map.put("yearSortList",yearSortList);
                map.put("languageSortList",languageSortList);
                map.put("authorSortList",authorSortList);
                map.put("organizationSortList",organizationSortList);

            } else {
                map.put("status", 441);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return map;
    }

    @PostMapping("/searchRank")
    public Map<String, Object> searchRank(@RequestBody Map<String, Object> params) {
        System.out.println(params);
        Map<String, Object> map = new HashMap<String, Object>();
        int pageSize = (int) params.get("pageSize");
        int pageNum = (int) params.get("pageNum");
        int rankWay = (int) params.get("rankWay");
        boolean f =true;
        ArrayList date = null;
        if(params.get("date").getClass()==String.class)
            f =false;
        else
            date = (ArrayList) params.get("date");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> conditionlist = (List<Map<String, Object>>) params.get("conditionList");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        for (Map<String, Object> condition : conditionlist) {
            int type = (int) condition.get("type");
            String context = (String) condition.get("context");
            int relationship = (int) condition.get("relationship");
            System.out.println(condition.get("isCurrent"));
            Boolean isCurrent;
            if((int) condition.get("isCurrent")==1){
                isCurrent =true;
            }
            else {
                isCurrent = false;
            }
            if (type == 0) {//全部检索
                if (isCurrent) {//精确
                    boolQueryBuilder.should(QueryBuilders.termQuery("title", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("abstracts", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("keywords", context));
                } else {//模糊
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("title", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("abstracts", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("keywords", context));
                }

            } else if (type == 1) {//题目检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("title", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("title", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("title", context));
                    }
                }
            } else if (type == 2) {//作者检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                }
            } else if (type == 3) {//作者单位检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                }
            } else if (type == 4) {//关键词检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                }
            } else if (type == 5) {//摘要检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                }
            } else if (type == 6) {//年份检索
                if (relationship == 1) {//与
                    boolQueryBuilder.must(QueryBuilders.termQuery("year", context));

                } else if (relationship == 2) {//或
                    boolQueryBuilder.should(QueryBuilders.termQuery("year", context));
                } else if (relationship == 3) {//非
                    boolQueryBuilder.mustNot(QueryBuilders.termQuery("year", context));
                }
            } else if (type == 7) {

            } else if (type == 8) {//作者检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                }
            } else if (type == 9) {//作者机构检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                }
            } else if (type == 10) {//作者检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                }
            } else if (type == 11) {//第一作者检索

            } else if (type == 12) {//作者机构检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                }
            }
        }
        if(f&&date.size()!=0)
            boolQueryBuilder.must(QueryBuilders.rangeQuery("year").from(date.get(0)).to(date.get(1)));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            //对Hits进行排序
            if (rankWay == 2) {//发表时间
                Arrays.sort(Hits, Comparator.comparingInt(o -> (int) (o.getSourceAsMap().get("year"))));
            } else if (rankWay == 3) {//被引量
                Arrays.sort(Hits, Comparator.comparingInt(o -> (int) (o.getSourceAsMap().get("n_citation"))));
            }

            int num = Hits.length;
            for(int i=0;i<(int)(num/2);i++){
                SearchHit temp = Hits[i];
                Hits[i]= Hits[num-i-1];
                Hits[num-i-1] = temp;
            }
            int page_number;
            if (num > 0) {
                int flag = num % pageSize;
                if (flag == 0) {
                    page_number = num / pageSize;
                } else {
                    page_number = num / pageSize + 1;
                }
                map.put("status", 200);
                map.put("paperNum", num);
                map.put("pageAllNum", page_number);
                List<Map<String, Object>> paperList = new ArrayList<Map<String, Object>>();
                List<String> yearSortList = new ArrayList<String>();
                List<String> languageSortList = new ArrayList<String>();
                List<String> authorSortList = new ArrayList<String>();
                List<String> organizationSortList = new ArrayList<String>();
                languageSortList.add("English("+num+")");
                Map<Integer,Integer> yearmap = new HashMap<>();
                Map<String,Integer> authormap = new HashMap<>();
                Map<String,Integer> orgnizationmap = new HashMap<>();
                for (SearchHit hit:Hits) {
                    if(!yearmap.containsKey((Integer) hit.getSourceAsMap().get("year"))){
                        yearmap.put((Integer)hit.getSourceAsMap().get("year"), 1);
                    }
                    else
                        yearmap.put((Integer)hit.getSourceAsMap().get("year"), yearmap.get((Integer) hit.getSourceAsMap().get("year"))+1);
                    List<Map<String,Object>> authorlist = (List<Map<String,Object>>)hit.getSourceAsMap().get("authors");
                    for (Map<String,Object>author:authorlist) {
                        if(!authormap.containsKey((String) author.get("name"))){
                            authormap.put((String) author.get("name"),1);
                        }
                        else
                            authormap.put((String) author.get("name"), authormap.get((String) author.get("name"))+1);
                    }
                    for (Map<String,Object>author:authorlist) {
                        if(!orgnizationmap.containsKey((String) author.get("org"))){
                            orgnizationmap.put((String) author.get("org"),1);
                        }
                        else
                            orgnizationmap.put((String) author.get("org"), orgnizationmap.get((String) author.get("org"))+1);
                    }
                }
                authormap.remove(null);
                orgnizationmap.remove(null);
                int i=0;
                for(Integer year:yearmap.keySet()){
                    yearSortList.add(year+"("+yearmap.get(year)+")");
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>10) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>5) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>3) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    authorSortList.add(name + "(" + authormap.get(name) + ")");
                    i++;
                }
                i=0;
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>100) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>30) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>10) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                    i++;
                }
                int control = (pageNum-1) * pageSize;
                while (control < pageNum * pageSize && control<num) {
                    SearchHit hit = Hits[control++];
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("title", hit.getSourceAsMap().get("title"));
                    String str = (String) hit.getSourceAsMap().get("abstract");
                    if (str.length()>200)
                        map1.put("abstract", str.substring(0, 200) + "....");
                    else
                        map1.put("abstract", str);
                    List<String> keyWords = (List<String>) hit.getSourceAsMap().get("keywords");
                    if(keyWords.size()>10){
                        map1.put("keyWords", keyWords.subList(0,10));
                    }
                    else
                        map1.put("keyWords", hit.getSourceAsMap().get("keywords"));
                    List<Map<String, Object>> list = (List<Map<String, Object>>) hit.getSourceAsMap().get("authors");
                    List<Map<String, Object>> list1 = new ArrayList<>();
                    if (list.size() >= 3) {
                        list1 = list.subList(0, 3);
                    }
                    map1.put("author", list1);
                    map1.put("venue", hit.getSourceAsMap().get("venue"));
                    map1.put("id", hit.getSourceAsMap().get("id"));
                    map1.put("year", hit.getSourceAsMap().get("year"));
                    paperList.add(map1);
                }
                map.put("paperList", paperList);
                map.put("yearSortList", yearSortList);
                map.put("languageSortList", languageSortList);
                map.put("authorSortList", authorSortList);
                map.put("organizationSortList", organizationSortList);
                System.out.println(map);

            } else {
                map.put("status", 441);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return map;
    }

    @PostMapping("/getInform")
    public Map<String, Object> getInform(@RequestBody Map<String, Object> params) {
        Map<String, Object> map = new HashMap<String, Object>();
       // HttpSession session = request.getSession();
        String context = (String) params.get("context");
        int type = (int) params.get("type");
        String id = (String) params.get("id");
        Report rep = new Report();
        rep.setContent(context);
        rep.setType(type);
        rep.setReporteeID12(id);
        rep.setReportTime(new Timestamp (new Date().getTime()));
        map.put("status", 200);
        reportRepository.save(rep);
        return map;
    }

    @PostMapping("/writeComment")
    public Map<String, Object> writeComment(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Map<String, Object> map = new HashMap<String, Object>();
        String context = (String) params.get("context");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String userid = (String) session.getAttribute("id");
        String paperid = (String) params.get("paperid");
        Comment comment = new Comment();
        comment.setCommentTime(new DateTime(new Date().getTime()));
        comment.setContent(context);
        comment.setUserID(userid);
        comment.setToID(paperid);
        commentRepository.save(comment);

        map.put("status", 200);
        return map;
    }

    @PostMapping("/getNextPage")
    public Map<String, Object> getNextPage(@RequestBody Map<String, Object> params) {
        System.out.println(params);
        Map<String, Object> map = new HashMap<String, Object>();
        int pageSize = (int) params.get("pageSize");
        int pageNum = (int) params.get("pageNum");
        boolean f =true;
        ArrayList date = null;
        if(params.get("date").getClass()==String.class)
            f =false;
        else
            date = (ArrayList) params.get("date");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> conditionlist = (List<Map<String, Object>>) params.get("conditionList");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        for (Map<String, Object> condition : conditionlist) {
            int type = (int) condition.get("type");
            String context = (String) condition.get("context");
            int relationship = (int) condition.get("relationship");
            Boolean isCurrent ;
            if((int) condition.get("isCurrent")==1){
                isCurrent =true;
            }
            else {
                isCurrent = false;
            }
            if (type == 0) {//全部检索
                if (isCurrent) {//精确
                    boolQueryBuilder.should(QueryBuilders.termQuery("title", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("abstracts", context));
                    boolQueryBuilder.should(QueryBuilders.termQuery("keywords", context));
                } else {//模糊
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("title", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("abstracts", context));
                    boolQueryBuilder.should(QueryBuilders.fuzzyQuery("keywords", context));
                }

            } else if (type == 1) {//题目检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("title", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("title", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("title", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("title", context));
                    }
                }
            } else if (type == 2) {//作者检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                }
            } else if (type == 3) {//作者单位检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                }
            } else if (type == 4) {//关键词检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("keywords", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("keywords", context));
                    }
                }
            } else if (type == 5) {//摘要检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("abstracts", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("abstracts", context));
                    }
                }
            } else if (type == 6) {//年份检索
                if (relationship == 1) {//与
                    boolQueryBuilder.must(QueryBuilders.termQuery("year", context));

                } else if (relationship == 2) {//或
                    boolQueryBuilder.should(QueryBuilders.termQuery("year", context));
                } else if (relationship == 3) {//非
                    boolQueryBuilder.mustNot(QueryBuilders.termQuery("year", context));
                }
            } else if (type == 7) {

            } else if (type == 8) {//作者检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                }
            } else if (type == 9) {//作者机构检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                }
            } else if (type == 10) {//作者检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.name", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.name", context));
                    }
                }
            } else if (type == 11) {//第一作者检索

            } else if (type == 12) {//作者机构检索
                if (relationship == 1) {//与
                    if (isCurrent) {//精确
                        boolQueryBuilder.must(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.must(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 2) {//或
                    if (isCurrent) {//精确
                        boolQueryBuilder.should(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.should(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                } else if (relationship == 3) {//非
                    if (isCurrent) {//精确
                        boolQueryBuilder.mustNot(QueryBuilders.termQuery("authors.org", context));
                    } else {//模糊
                        boolQueryBuilder.mustNot(QueryBuilders.fuzzyQuery("authors.org", context));
                    }
                }
            }
        }
        if(f&&date.size()!=0)
            boolQueryBuilder.must(QueryBuilders.rangeQuery("year").from(date.get(0)).to(date.get(1)));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            int num = Hits.length;
            int page_number;
            if (num > 0) {
                int flag = num % pageSize;
                if (flag == 0) {
                    page_number = num / pageSize;
                } else {
                    page_number = num / pageSize + 1;
                }
                map.put("status", 200);
                map.put("paperNum", num);
                map.put("pageAllNum", page_number);
                List<Map<String, Object>> paperList = new ArrayList<Map<String, Object>>();
                List<String> yearSortList = new ArrayList<String>();
                List<String> languageSortList = new ArrayList<String>();
                List<String> authorSortList = new ArrayList<String>();
                List<String> organizationSortList = new ArrayList<String>();
                languageSortList.add("English("+num+")");
                Map<Integer,Integer> yearmap = new HashMap<>();
                Map<String,Integer> authormap = new HashMap<>();
                Map<String,Integer> orgnizationmap = new HashMap<>();
                for (SearchHit hit:Hits) {
                    if(!yearmap.containsKey((Integer) hit.getSourceAsMap().get("year"))){
                        yearmap.put((Integer)hit.getSourceAsMap().get("year"), 1);
                    }
                    else
                        yearmap.put((Integer)hit.getSourceAsMap().get("year"), yearmap.get((Integer) hit.getSourceAsMap().get("year"))+1);
                    List<Map<String,Object>> authorlist = (List<Map<String,Object>>)hit.getSourceAsMap().get("authors");
                    for (Map<String,Object>author:authorlist) {
                        if(!authormap.containsKey((String) author.get("name"))){
                            authormap.put((String) author.get("name"),1);
                        }
                        else
                            authormap.put((String) author.get("name"), authormap.get((String) author.get("name"))+1);
                    }
                    for (Map<String,Object>author:authorlist) {
                        if(!orgnizationmap.containsKey((String) author.get("org"))){
                            orgnizationmap.put((String) author.get("org"),1);
                        }
                        else
                            orgnizationmap.put((String) author.get("org"), orgnizationmap.get((String) author.get("org"))+1);
                    }
                }
                authormap.remove(null);
                orgnizationmap.remove(null);
                int i=0;
                for(Integer year:yearmap.keySet()){
                    yearSortList.add(year+"("+yearmap.get(year)+")");
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>10) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>5) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    if(authormap.get(name)>3) {
                        authorSortList.add(name + "(" + authormap.get(name) + ")");
                        i++;
                    }
                }
                for(String name:authormap.keySet()){
                    if(i>100)
                        break;
                    authorSortList.add(name + "(" + authormap.get(name) + ")");
                    i++;
                }
                i=0;
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>100) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>30) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    if(orgnizationmap.get(org)>10) {
                        organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                        i++;
                    }
                }
                for (String org:orgnizationmap.keySet()) {
                    if(i>100){
                        break;
                    }
                    organizationSortList.add(org + "(" + orgnizationmap.get(org) + ")");
                    i++;
                }
                int control = (pageNum-1) * pageSize;
                while (control < pageNum * pageSize && control<num) {
                    SearchHit hit = Hits[control++];
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("title", hit.getSourceAsMap().get("title"));
                    String str = (String) hit.getSourceAsMap().get("abstract");
                    if (str.length()>200)
                        map1.put("abstract", str.substring(0, 200) + "....");
                    else
                        map1.put("abstract", str);
                    List<String> keyWords = (List<String>) hit.getSourceAsMap().get("keywords");
                    if(keyWords.size()>10){
                        map1.put("keyWords", keyWords.subList(0,10));
                    }
                    else
                        map1.put("keyWords", hit.getSourceAsMap().get("keywords"));
                    List<Map<String, Object>> list = (List<Map<String, Object>>) hit.getSourceAsMap().get("authors");
                    List<Map<String, Object>> list1 = new ArrayList<>();
                    if (list.size() >= 3) {
                        list1 = list.subList(0, 3);
                    }
                    map1.put("author", list1);
                    map1.put("venue", hit.getSourceAsMap().get("venue"));
                    map1.put("id", hit.getSourceAsMap().get("id"));
                    map1.put("year", hit.getSourceAsMap().get("year"));
                    paperList.add(map1);
                }
                map.put("paperList", paperList);
                map.put("yearSortList",yearSortList);
                map.put("languageSortList",languageSortList);
                map.put("authorSortList",authorSortList);
                map.put("organizationSortList",organizationSortList);

            } else {
                map.put("status", 441);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return map;
    }
    @PostMapping("/searchAuthor")
    public Map<String, Object> searchAuthor(@RequestBody Map<String, Object> params) {
        String condition = "*"+(String)params.get("condition")+"*";
        int pageNum = (int)params.get("pageNum");
        System.out.println(condition+pageNum);
        Map<String, Object> map = new HashMap<String, Object>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("author");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("name",condition));
        //增加查询条件
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            int allnum = Hits.length;
            if(allnum==0){
                map.put("status",441);
                return map;
            }
            ArrayList<Map<String,Object>> authorlist = new ArrayList<>();
            int allpage;
            if(allnum%20==0){
                allpage = allnum/20;
            }
            else{
                allpage = allnum/20 + 1;
            }
              for(int i = (pageNum-1)*20;i<pageNum*20&&i<allnum;i++) {
                Map<String,Object> author = new HashMap<>();
                author.put("name",(String)Hits[i].getSourceAsMap().get("name"));
                author.put("id",(String)Hits[i].getSourceAsMap().get("id"));
                author.put("org",(String)Hits[i].getSourceAsMap().get("org"));
                author.put("position",(String)Hits[i].getSourceAsMap().get("position"));
                if(Hits[i].getSourceAsMap().get("n_citation")!=null)
                author.put("citation",(int)Hits[i].getSourceAsMap().get("n_citation"));
                else
                    author.put("citation",0);
                ArrayList<String> skills  = new ArrayList<>();
                ArrayList<Map<String,Object>> tags = (ArrayList<Map<String,Object>>)Hits[i].getSourceAsMap().get("tags");
                int p = 0;
                  for (Map<String,Object> tag:tags) {
                      if(p++>2)
                          break;
                      skills.add((String) tag.get("t"));
                  }
                  author.put("skills",skills);
                  authorlist.add(author);
              }
              map.put("status",200);
              map.put("authorList",authorlist);
              map.put("pageNum",allpage);
              map.put("allNum",allnum);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return map;
    }
    @PostMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> map = new HashMap<String, Object>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("authors.name", "john"));
        //增加查询条件
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            int i = 0;
            for (SearchHit searchHit : Hits) {
                String name = (String) searchHit.getSourceAsMap().get("title");
                Integer birth = (Integer) searchHit.getSourceAsMap().get("year");
                String interest = (String) searchHit.getSourceAsMap().get("abstract");
                System.out.println("-------------" + (++i) + "------------");
                System.out.println(name);
                System.out.println(birth);
                System.out.println(interest);
                System.out.println(searchHit.getSourceAsMap().get("keywords"));
                if (i > 5)
                    break;
            }
            Map<String, Object> map1 = new HashMap<>();
            map1.put("test_", 1);
            map.put("test", map1);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return map;
    }
}