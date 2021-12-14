package com.example.portal.controller;

import com.example.portal.dao.AuthorRepository;
import com.example.portal.entity.Author;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap;
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
import org.springframework.http.server.DelegatingServerHttpResponse;
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

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            SearchRequest searchRequest = new SearchRequest("paper");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("authors.id", id));
            searchSourceBuilder.query(boolQueryBuilder);
            searchSourceBuilder.size(10000);
            searchRequest.source(searchSourceBuilder);
            try {
                SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
                SearchHits searchHits = searchResponse.getHits();
                long totalhits = searchHits.getTotalHits().value;
                System.out.println(totalhits);
                List<Essay> essayList = new LinkedList<>();
                Map<String, Integer> hype = new LinkedHashMap<>();
                for(SearchHit sh : searchHits) {
                    Map<?,?> elem = sh.getSourceAsMap();
                    List<Map<String, String>> author_ = new ArrayList<>();
                    List<String> keywords = (List<String>) elem.get("keywords");
                    for(String k : keywords) {
                        if(null != hype.get(k))
                            hype.replace(k, hype.get(k) + 1);
                        else
                            hype.put(k, 1);
                    }
                    List<Map<?,?>> authors = (List<Map<?,?>>) elem.get("authors");
                    for(Map<?,?> m : authors) {
                        Map<String, String> temp = new HashMap<>();
                        temp.put("name", (String) m.get("name"));
                        temp.put("id", (String) m.get("id"));
                        author_.add(temp);
                    }
                    essayList.add(new Essay((String)elem.get("id") ,(String)elem.get("title"), author_, (String)elem.get("abstract"),
                            (Integer) elem.get("year"), (String) ((Map<?,?>)elem.get("venue")).get("raw"), (List<String>) elem.get("keywords"), (Integer) elem.get("n_citation")));
                }
                essayList.sort((o1, o2) -> o2.getYear() - o1.getYear());
                List<String> fields = new LinkedList<>(hype.keySet());
                fields.sort((o1, o2) -> {
                    int i1 = hype.get(o1);
                    int i2 = hype.get(o2);
                    return i2 - i1;
                });
                ret.put("fields", (T) fields.subList(0, (5 > fields.size()? fields.size() : 5)));
                ret.put("essayList", (T) essayList);
                ret.put("relevantAuthor", (T) essayList.get(0).author);
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
            Map<String, Integer> hype = new LinkedHashMap<>();
            for(SearchHit sh : searchHits) {
                Map<?,?> elem = sh.getSourceAsMap();
                List<Map<String, String>> author = new ArrayList<>();
                List<Map<?,?>> authors = (List<Map<?,?>>) elem.get("authors");
                List<String> keywords = (List<String>) elem.get("keywords");
                for(String k : keywords) {
                    if(null != hype.get(k))
                        hype.replace(k, hype.get(k) + 1);
                    else
                        hype.put(k, 1);
                }
                for(Map<?,?> m : authors) {
                    Map<String, String> temp = new HashMap<>();
                    temp.put("name", (String) m.get("name"));
                    temp.put("id", (String) m.get("id"));
                    author.add(temp);
                }
                essayList.add(new Essay((String)elem.get("id") ,(String)elem.get("title"), author, (String)elem.get("abstract"),
                        (Integer) elem.get("year"), (String) ((Map<?,?>)elem.get("venue")).get("raw"), (List<String>) elem.get("keywords"), (Integer) elem.get("n_citation")));
            }
            essayList.sort(new Comparator<Essay>() {
                @Override
                public int compare(Essay o1, Essay o2) {
                    return o2.getNc() - o1.getNc();
                }
            });
            List<String> fields = new LinkedList<>(hype.keySet());
            fields.sort((o1, o2) -> {
                int i1 = hype.get(o1);
                int i2 = hype.get(o2);
                return i2 - i1;
            });
            ret.put("fields", (T) fields.subList(0, (5 > fields.size()? fields.size() : 5)));
            ret.put("essayList", (T) essayList);
            ret.put("relevantAuthor", (T) essayList.get(0).author);
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
            Map<String, Integer> hype = new LinkedHashMap<>();
            for(SearchHit sh : searchHits) {
                Map<?,?> elem = sh.getSourceAsMap();
                List<Map<String, String>> author = new ArrayList<>();
                List<Map<?,?>> authors = (List<Map<?,?>>) elem.get("authors");
                List<String> keywords = (List<String>) elem.get("keywords");
                for(String k : keywords) {
                    if(null != hype.get(k))
                        hype.replace(k, hype.get(k) + 1);
                    else
                        hype.put(k, 1);
                }
                for(Map<?,?> m : authors) {
                    Map<String, String> temp = new HashMap<>();
                    temp.put("name", (String) m.get("name"));
                    temp.put("id", (String) m.get("id"));
                    author.add(temp);
                }
                essayList.add(new Essay((String)elem.get("id"), (String)elem.get("title"), author, (String)elem.get("abstract"),
                        (Integer) elem.get("year"), (String) ((Map<?,?>)elem.get("venue")).get("raw"), (List<String>) elem.get("keywords"), (Integer) elem.get("n_citation")));
            }
            essayList.sort(new Comparator<Essay>() {
                @Override
                public int compare(Essay o1, Essay o2) {
                    return o2.getYear() - o1.getYear();
                }
            });
            List<String> fields = new LinkedList<>(hype.keySet());
            fields.sort((o1, o2) -> {
                int i1 = hype.get(o1);
                int i2 = hype.get(o2);
                return i2 - i1;
            });
            ret.put("fields", (T) fields.subList(0, (5 > fields.size()? fields.size() : 5)));
            ret.put("essayList", (T) essayList);
            Iterator<Essay> iter = essayList.listIterator();
            Essay target = iter.next();
            while(iter.hasNext()) {
                Essay temp = iter.next();
                if(temp.getNc() > target.getNc())
                    target = temp;
            }
            ret.put("relevantAuthor", (T) target.getAuthor());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return ret;
    }

}

@Data
@AllArgsConstructor
class Essay {
    String id;
    String title;
    List<Map<String, String>> author;
    String abstract_;
    int year;
    String source;
    List<String> theme;
    int nc;
}
