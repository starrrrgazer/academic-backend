package com.example.literature.controller;
// 接受前端请求去处理，用于前后端交互，第一层，之后调用服务层service实现后端的逻辑
import com.example.literature.dao.PaperRepository;
import com.example.literature.entity.AuthorList;
import com.example.literature.entity.Paper;
import com.example.literature.entity.Venue;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/searchPaper")
    public Map<String, Object> searchPaper(@RequestBody Map<String,Object> params){
        System.out.println(params);
        Map<String,Object> map = new HashMap<String,Object>();
        int pageSize = (int) params.get("pageSize");
        int pageNum = (int) params.get("pageNum");
        /*String[] date_init = (String[])params.get("date");
        int startdate = Integer.parseInt(date_init[0]);
        int enddate = Integer.parseInt(date_init[1]);*/
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> conditionlist = (List<Map<String,Object>>) params.get("conditionList");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //建空查询
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        for (Map<String,Object> condition:conditionlist) {
            int type = (int) condition.get("type");
            String context = (String) condition.get("context");
            int relationship = (int) condition.get("relationship");
            boolean isCurrent = (boolean) condition.get("isCurrent");
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

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);

        try{
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] Hits = searchHits.getHits();
            int num = Hits.length;
            int page_number;
            if (num >0) {
                int flag = num % pageSize;
                if (flag == 0) {
                    page_number = num/pageSize;
                }
                else{
                    page_number = num/pageSize+1;
                }
                map.put("status",200);
                map.put("paperNum",num);
                map.put("pageAllNum",page_number);
                List<Map<String,Object>> paperList = new ArrayList<Map<String,Object>>();
                List<String> yearSortList = new ArrayList<String>();
                List<String> languageSortList = new ArrayList<String>();
                List<String> authorSortList = new ArrayList<String>();
                List<String> organizationSortList = new ArrayList<String>();
                yearSortList.add("1999(100)");
                yearSortList.add("2000(100)");
                authorSortList.add("john(10)");
                authorSortList.add("jack(32)");
                organizationSortList.add("buaa(3)");
                languageSortList.add("English(10)");
                int control = pageNum*pageSize;
                while (control< pageNum*pageSize+pageSize) {
                    SearchHit hit = Hits[control++];
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("title",hit.getSourceAsMap().get("title"));
                    String str = (String) hit.getSourceAsMap().get("abstract");
                    map1.put("abstract",str.substring(0,50)+"....");
                    map1.put("keyWords",hit.getSourceAsMap().get("keywords"));
                    List<Map<String,Object>> list= (List<Map<String,Object>>)hit.getSourceAsMap().get("authors");
                    List<Map<String,Object>> list1 = new ArrayList<>();
                    if(list.size()>=3){
                        list1 = list.subList(0,3);
                    }
                    map1.put("author",list1);
                    map1.put("venue",hit.getSourceAsMap().get("venue"));
                    map1.put("id",hit.getSourceAsMap().get("id"));
                    map1.put("year",hit.getSourceAsMap().get("year"));
                    paperList.add(map1);
                }
                map.put("paperList",paperList);
                map.put("yearSortList",yearSortList);
                map.put("languageSortList",languageSortList);
                map.put("authorSortList",authorSortList);
                map.put("organizationSortList",organizationSortList);

            }
            else{
                map.put("status",441);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return map;
    }


    @PostMapping("/recommendPaper")
    public Map<String, Object> recommendPaper(@RequestBody Map<String,Object> params) {
        Map<String,Object> map = new HashMap<String,Object>();
        return map;
    }



    @PostMapping("/getPaperdetail")
    public Map<String, Object> getPaperdetail(@RequestBody Map<String,Object> params) {
        System.out.println(params);
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
            if(Hits.length>0) {
                SearchHit searchHit = Hits[0];
                map.put("language", "english");
                map.put("title", searchHit.getSourceAsMap().get("title"));
                map.put("citiation",  searchHit.getSourceAsMap().get("n_citation"));
                map.put("issn", searchHit.getSourceAsMap().get("issn"));
                map.put("doi", searchHit.getSourceAsMap().get("doi"));
                System.out.println(2);
                map.put("abstract", searchHit.getSourceAsMap().get("abstract"));
                map.put("venue", searchHit.getSourceAsMap().get("venue"));
                map.put("year", searchHit.getSourceAsMap().get("year"));
                map.put("pdf", searchHit.getSourceAsMap().get("pdf"));
                System.out.println(3);
                map.put("authors", searchHit.getSourceAsMap().get("authors"));
                map.put("keyWords", searchHit.getSourceAsMap().get("keywords"));
                map.put("urls", searchHit.getSourceAsMap().get("url"));
                System.out.println(4);
                map.put("relevantScholars", searchHit.getSourceAsMap().get("authors"));
                map.put("status",200);
            }
            else
                map.put("status",441);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        System.out.println(5);
        return map;
    }


    @PostMapping("/classificationPaper")
    public Map<String, Object> classificationPaper(@RequestBody Map<String,Object> params) {
        Map<String,Object> map = new HashMap<String,Object>();

        return map;
    }
    @PostMapping("/test")
    public Map<String, Object> test() {
        Map<String,Object> map = new HashMap<String,Object>();
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
            int i=0;
            for (SearchHit searchHit : Hits) {
                String name = (String) searchHit.getSourceAsMap().get("title");
                Integer birth = (Integer) searchHit.getSourceAsMap().get("year");
                String interest = (String) searchHit.getSourceAsMap().get("abstract");
                System.out.println("-------------" + (++i) + "------------");
                System.out.println(name);
                System.out.println(birth);
                System.out.println(interest);
                System.out.println(searchHit.getSourceAsMap().get("keywords"));
                if(i>5)
                    break;
            }
            Map<String,Object> map1 = new HashMap<>();
            map1.put("test_",1);
            map.put("test",map1);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return map;
    }
}
