package com.example.portal.controller;

import com.example.portal.dao.AuthorRepository;
import com.example.portal.entity.Author;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true",maxAge =
        3600)
@RestController
public class PortalController {
    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    RestHighLevelClient restHighLevelClient;


    @PostMapping("/getUserInfo")
    public <T extends Object> Map<String, T> getUserInfo(@RequestBody Map<String, String> remap) {
        Map<String, T> ret = new HashMap<>();
        String id = remap.get("userID");
        Optional<Author> query = authorRepository.findById(id);
        if (query.isPresent()) {
            Author author = query.get();
            ret.put("success", (T) "true");
            ret.put("citeEssayNum", (T) ("" + author.getN_citation()));
            ret.put("coreEaasyNum", (T) ("" + author.getN_pubs()));
            ret.put("avgciteEssayNum", (T) ("" + author.getN_citation() / author.getN_pubs()));
            ret.put("userName", (T) author.getName());
            ret.put("unit", (T) new ArrayList<String>(Arrays.asList(author.getOrg().split(",\\s+"))));

            System.out.println(1);
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            SearchRequest searchRequest = new SearchRequest("paper");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("authors.id", id));
            searchSourceBuilder.query(boolQueryBuilder);
            searchSourceBuilder.size(10000);
            searchRequest.source(searchSourceBuilder);
            System.out.println(2);
            try {
                SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
                SearchHits searchHits = searchResponse.getHits();
                long totalhits = searchHits.getTotalHits().value;
                System.out.println(totalhits);
                System.out.println(3);
                List<Essay> essayList = new LinkedList<>();
                for(SearchHit sh : searchHits) {
                    Map<?,?> elem = sh.getSourceAsMap();
                    List<String> author_ = new ArrayList<>();
                    List<Map<?,?>> authors = (List<Map<?,?>>) elem.get("authors");
                    for(Map<?,?> m : authors)
                        author_.add((String) m.get("name"));
                    essayList.add(new Essay((String)elem.get("title"), author_, (String)elem.get("abstract"),
                            (Integer) elem.get("year"), (String) ((Map<?,?>)elem.get("venue")).get("raw"), (List<String>) elem.get("keywords"), (Integer) elem.get("n_citation")));
                }
                System.out.println(4);
                essayList.sort(new Comparator<Essay>() {
                    @Override
                    public int compare(Essay o1, Essay o2) {
                        return o2.getYear() - o1.getYear();
                    }
                });
                ret.put("essayList", (T) essayList);
                System.out.println(5);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }


//            List<Paper> paperList = paperRepository.findAllByAuthorOrderByNCitationDesc(id);
//            List<Essay> essayList = new LinkedList<>();
//            for(Paper p : paperList) {
//                List<Map<?,?>> all = p.getAuthors();
//                List<String> authors = new LinkedList<>();
//                for(Map<?,?> cur: all)
//                    authors.add((String)cur.get("name"));
//                essayList.add(new Essay(p.getTitle(), authors, p.getAbstract_(), p.getYear(), (String) p.getVenue().get("raw"),
//                       p.getKeywords()));
//            }
//            ret.put("essayList", (T) essayList);

        }
        else {
            ret.put("success", (T) "false");
            ret.put("msg", (T) "找不到该作者");
        }
        return ret;
    }


    @PostMapping("/getHighCiteList/{aid}")
    public <T extends Object> Map<String, T> getHighCiteList(HttpServletRequest request, @PathVariable("aid") String aid) {
        Map<String, T> ret = new HashMap<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("authors.id", aid));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            long totalhits = searchHits.getTotalHits().value;
            System.out.println(totalhits);
            List<Essay> essayList = new LinkedList<>();
            for(SearchHit sh : searchHits) {
                Map<?,?> elem = sh.getSourceAsMap();
                List<String> author = new ArrayList<>();
                List<Map<?,?>> authors = (List<Map<?,?>>) elem.get("authors");
                for(Map<?,?> m : authors)
                    author.add((String) m.get("name"));
                essayList.add(new Essay((String)elem.get("title"), author, (String)elem.get("abstract"),
                        (Integer) elem.get("year"), (String) ((Map<?,?>)elem.get("venue")).get("raw"), (List<String>) elem.get("keywords"), (Integer) elem.get("n_citation")));
            }
            essayList.sort(new Comparator<Essay>() {
                @Override
                public int compare(Essay o1, Essay o2) {
                    return o2.getNc() - o1.getNc();
                }
            });
            ret.put("essayList", (T) essayList);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return ret;
    }

    @PostMapping("/getNewPostList/{aid}")
    public <T extends Object> Map<String, T> getNewPostList(HttpServletRequest request, @PathVariable("aid") String aid) {
        Map<String, T> ret = new HashMap<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("authors.id", aid));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            System.out.println(1);
            long totalhits = searchHits.getTotalHits().value;
            System.out.println(totalhits);
            List<Essay> essayList = new LinkedList<>();
            for(SearchHit sh : searchHits) {
                Map<?,?> elem = sh.getSourceAsMap();
                List<String> author = new ArrayList<>();
                List<Map<?,?>> authors = (List<Map<?,?>>) elem.get("authors");
                for(Map<?,?> m : authors)
                    author.add((String) m.get("name"));
                essayList.add(new Essay((String)elem.get("title"), author, (String)elem.get("abstract"),
                        (Integer) elem.get("year"), (String) ((Map<?,?>)elem.get("venue")).get("raw"), (List<String>) elem.get("keywords"), (Integer) elem.get("n_citation")));
            }
            System.out.println(2);
            essayList.sort(new Comparator<Essay>() {
                @Override
                public int compare(Essay o1, Essay o2) {
                    return o2.getYear() - o1.getYear();
                }
            });
            System.out.println(3);
            ret.put("essayList", (T) essayList);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return ret;
    }

}

@Data
@AllArgsConstructor
class Essay {
    String title;
    List<String> author;
    String abstract_;
    int year;
    String source;
    List<String> theme;
    int nc;
}
