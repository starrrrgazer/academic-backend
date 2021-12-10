package com.example.management.Service;

import com.example.management.Entity.AuthorList;
import com.example.management.Entity.Paper;
import com.example.management.Entity.Venue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class PaperService {


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public Map<String, Object> addPaper(Map<String,Object> map){
        Map<String, Object> returnObject = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> newPaperMap = new HashMap<>();
        IndexRequest request = new IndexRequest();
        request.index("paper");
//        Paper newPaper = new Paper();
        try{
            //id
            String id = (String) map.get("id");
//            Paper a = paperMapper.getPaperById(id);
//            if (a!=null){
//                returnObject.put("status","402");
//                returnObject.put("result","文章已存在");
//                return returnObject;
//            }
            newPaperMap.put("id",id);
            //authors
            List<Map<String, Object>> authors = Collections.singletonList((Map<String, Object>) map.get("authors"));
            List<AuthorList> authorList = new ArrayList<>();
            for (int i = 0; i < authors.size(); i++) {
                AuthorList tmp = new AuthorList(authors.get(i));
                authorList.add(tmp);
            }
            newPaperMap.put("authors",authorList);
//            newPaper.setAuthors(authorList);
            //title
            String title = (String) map.get("title");
            newPaperMap.put("title",title);
//            newPaper.setTitle(title);
            //year
            int year = (int) map.get("year");
            newPaperMap.put("year",year);
//            newPaper.setYear(year);
            //n_citation
            int n_citation = (int) map.get("n_citation");
//            newPaper.setN_citation(n_citation);
            newPaperMap.put("n_citation",n_citation);
            //url
            List<String> getUrls = Collections.singletonList((String) map.get("url"));
//            newPaper.setUrl();
            newPaperMap.put("url",getUrls);
            //doi
            String doi = (String) map.get("doi");
            newPaperMap.put("doi",doi);
            //page_start
            newPaperMap.put("page_start", map.get("page_start"));
            //page_end
            newPaperMap.put("page_end", map.get("page_end"));
            //abstracts
            String abstracts = (String) map.get("abstracts");
            newPaperMap.put("abstract",abstracts);
            //issue
            newPaperMap.put("issue",(String)map.get("issue"));
            //volume
            newPaperMap.put("volume",(String)map.get("volume"));
            //issn
            newPaperMap.put("issn",(String)map.get("issn"));
            //isbn
            newPaperMap.put("isbn",(String)map.get("isbn"));
            //pdf
            newPaperMap.put("pdf",(String)map.get("pdf"));
            //keywords
            List<String> keywords = (List<String>) map.get("keywords");
            newPaperMap.put("keywords",keywords);
            //venue
            Map<String, Object> getVenue = (Map<String, Object>) map.get("venue");
                Venue venue = new Venue((String) getVenue.get("id"), (String) getVenue.get("raw"));
            newPaperMap.put("venue",venue);

            Paper newPaper = new Paper(newPaperMap);
//            paperMapper.addPaper(newPaper);
            String paperJson = mapper.writeValueAsString(newPaper);
            request.source(paperJson, XContentType.JSON);

        } catch (Exception e) {
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }

        returnObject.put("status","200");
        returnObject.put( "result","添加成功");
        return returnObject;
    }

    public Map<String,Object> deletePaper(Map<String,Object> map){
        String id = (String) map.get("id");
        Map<String,Object> returnObject = new HashMap<>();

        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index("paper").id(id);
        try {
            DeleteResponse response = restHighLevelClient.delete(deleteRequest,RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            returnObject.put("status","401");
            returnObject.put("result","删除成功");
        }
        returnObject.put("status","200");
        returnObject.put("result","删除成功");
        return returnObject;
    }

    public Map<String,Object> getArticleList(Map<String,Object> map){
        String id = (String) map.get("id");
        Map<String,Object> returnObject = new HashMap<>();
        List<Map<String,Object>> papers = new ArrayList<>();
       BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        SearchRequest searchRequest = new SearchRequest("paper");
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
            tmp.put("title", hit.getSourceAsMap().get("title"));
            List<Map<String, Object>> list = (List<Map<String, Object>>) hit.getSourceAsMap().get("authors");
            tmp.put("authors",list);
            tmp.put("year", hit.getSourceAsMap().get("year"));
            tmp.put("keyWords", hit.getSourceAsMap().get("keywords"));
            tmp.put("abstract", hit.getSourceAsMap().get("abstract"));
            tmp.put("doi",hit.getSourceAsMap().get("doi"));
            tmp.put("pdf", hit.getSourceAsMap().get("pdf"));
            tmp.put("venue", hit.getSourceAsMap().get("venue"));
            papers.add(tmp);
        } catch (Exception e) {
            returnObject.put("status","403");
            returnObject.put("result","未知错误");
        }
        returnObject.put("status","200");
        returnObject.put("result","处理成功");
        returnObject.put("articleList",papers);
        return returnObject;
    }
}
