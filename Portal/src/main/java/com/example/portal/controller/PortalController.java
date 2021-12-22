package com.example.portal.controller;

import com.example.portal.dao.*;
import com.example.portal.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.DelegatingServerHttpResponse;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443",
        "https://localhost:8000","https://localhost:80","https://localhost:443",
        "http://121.36.60.6:8000","http://121.36.60.6:80","http://121.36.60.6:443",
        "https://121.36.60.6:8000","https://121.36.60.6:80","https://121.36.60.6:443"},allowCredentials = "true",maxAge = 3600)
@RestController
public class PortalController {
    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    PaperRepository paperRepository;

    @Autowired
    ReportRepository reportRepository;

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
                            (Integer) elem.get("year"), (String) ((Map<?,?>)elem.get("venue")).get("name"), (List<String>) elem.get("keywords"), (Integer) elem.get("n_citation")));
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

                List<Map<String, Object>> rela = new LinkedList<>();
                for(Map<String, String> a : essayList.get(0).author) {
                    Optional<Author> tag = authorRepository.findById(a.get("id"));
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("name", a.get("name"));
                    temp.put("id", a.get("id"));
                    if(tag.isPresent()) {
                        temp.put("unit", Arrays.asList(tag.get().getOrg().split(",\\s+")));
                        BoolQueryBuilder boolQueryBuilder_ = QueryBuilders.boolQuery();
                        SearchRequest searchRequest_ = new SearchRequest("paper");
                        SearchSourceBuilder searchSourceBuilder_ = new SearchSourceBuilder();
                        boolQueryBuilder_.must(QueryBuilders.matchPhraseQuery("authors.id", a.get("id")));
                        searchSourceBuilder_.query(boolQueryBuilder);
                        searchSourceBuilder_.size(10000);
                        searchRequest_.source(searchSourceBuilder);
                        SearchResponse searchResponse_ = restHighLevelClient.search(searchRequest_, RequestOptions.DEFAULT);
                        SearchHits searchHits_ = searchResponse_.getHits();

                        Map<String, Integer> hype_ = new HashMap<>();
                        for(SearchHit sh : searchHits_) {
                            Map<?,?> elem = sh.getSourceAsMap();
                            List<String> keywords = (List<String>) elem.get("keywords");
                            for(String k : keywords) {
                                if(null != hype.get(k))
                                    hype_.replace(k, hype.get(k) + 1);
                                else
                                    hype_.put(k, 1);
                            }
                        }
                        List<String> fields_ = new ArrayList<>(hype_.keySet());
                        fields_.sort((o1, o2) -> hype_.get(o2) - hype_.get(o1));
                        temp.put("fields", fields.subList(0, (3 > fields.size()? fields.size() : 3)));
                    }
                    rela.add(temp);
                }
                ret.put("relevantAuthor", (T) rela);

                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
                HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
                String username = (String) session.getAttribute("username");

                if(username == null)
                    ret.put("ifself", (T) "-1");
                else {
                    User owner = userRepository.findByAuthorID(id);
                    if(owner == null)
                        ret.put("ifself", (T) "0");
                    else {
                        if(owner.getUserIdentity() == 3) {
                            if(owner.getUsername().equals(username))
                                ret.put("ifself", (T) "1");
                            else
                                ret.put("ifself", (T) "2");
                        }
                        else {
                            if(owner.getUsername().equals(username))
                                ret.put("ifself", (T) "3");
                            else
                                ret.put("ifself", (T) "4");
                        }
                    }

                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

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
                        (Integer) elem.get("year"), (String) ((Map<?,?>)elem.get("venue")).get("name"), (List<String>) elem.get("keywords"), (Integer) elem.get("n_citation")));
            }
            essayList.sort((o1, o2) -> o2.getNc() - o1.getNc());
            List<String> fields = new LinkedList<>(hype.keySet());
            fields.sort((o1, o2) -> {
                int i1 = hype.get(o1);
                int i2 = hype.get(o2);
                return i2 - i1;
            });
            ret.put("fields", (T) fields.subList(0, (5 > fields.size()? fields.size() : 5)));
            ret.put("essayList", (T) essayList);
            List<Map<String, Object>> rela = new LinkedList<>();
            for(Map<String, String> a : essayList.get(0).author) {
                Optional<Author> tag = authorRepository.findById(a.get("id"));
                Map<String, Object> temp = new HashMap<>();
                temp.put("name", a.get("name"));
                temp.put("id", a.get("id"));
                if(tag.isPresent()) {
                    temp.put("unit", Arrays.asList(tag.get().getOrg().split(",\\s+")));
                    BoolQueryBuilder boolQueryBuilder_ = QueryBuilders.boolQuery();
                    SearchRequest searchRequest_ = new SearchRequest("paper");
                    SearchSourceBuilder searchSourceBuilder_ = new SearchSourceBuilder();
                    boolQueryBuilder_.must(QueryBuilders.matchPhraseQuery("authors.id", a.get("id")));
                    searchSourceBuilder_.query(boolQueryBuilder);
                    searchSourceBuilder_.size(10000);
                    searchRequest_.source(searchSourceBuilder);
                    SearchResponse searchResponse_ = restHighLevelClient.search(searchRequest_, RequestOptions.DEFAULT);
                    SearchHits searchHits_ = searchResponse_.getHits();

                    Map<String, Integer> hype_ = new HashMap<>();
                    for(SearchHit sh : searchHits_) {
                        Map<?,?> elem = sh.getSourceAsMap();
                        List<String> keywords = (List<String>) elem.get("keywords");
                        for(String k : keywords) {
                            if(null != hype.get(k))
                                hype_.replace(k, hype.get(k) + 1);
                            else
                                hype_.put(k, 1);
                        }
                    }
                    List<String> fields_ = new ArrayList<>(hype_.keySet());
                    fields_.sort((o1, o2) -> hype_.get(o2) - hype_.get(o1));
                    temp.put("fields", fields.subList(0, (3 > fields.size()? fields.size() : 3)));
                }
                rela.add(temp);
            }
            ret.put("relevantAuthor", (T) rela);
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
                        (Integer) elem.get("year"), (String) ((Map<?,?>)elem.get("venue")).get("name"), (List<String>) elem.get("keywords"), (Integer) elem.get("n_citation")));
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

            List<Map<String, Object>> rela = new LinkedList<>();
            for(Map<String, String> a : target.getAuthor()) {
                Optional<Author> tag = authorRepository.findById(a.get("id"));
                Map<String, Object> temp = new HashMap<>();
                temp.put("name", a.get("name"));
                temp.put("id", a.get("id"));
                if(tag.isPresent()) {
                    temp.put("unit", Arrays.asList(tag.get().getOrg().split(",\\s+")));
                    BoolQueryBuilder boolQueryBuilder_ = QueryBuilders.boolQuery();
                    SearchRequest searchRequest_ = new SearchRequest("paper");
                    SearchSourceBuilder searchSourceBuilder_ = new SearchSourceBuilder();
                    boolQueryBuilder_.must(QueryBuilders.matchPhraseQuery("authors.id", a.get("id")));
                    searchSourceBuilder_.query(boolQueryBuilder);
                    searchSourceBuilder_.size(10000);
                    searchRequest_.source(searchSourceBuilder);
                    SearchResponse searchResponse_ = restHighLevelClient.search(searchRequest_, RequestOptions.DEFAULT);
                    SearchHits searchHits_ = searchResponse_.getHits();

                    Map<String, Integer> hype_ = new HashMap<>();
                    for(SearchHit sh : searchHits_) {
                        Map<?,?> elem = sh.getSourceAsMap();
                        List<String> keywords = (List<String>) elem.get("keywords");
                        for(String k : keywords) {
                            if(null != hype.get(k))
                                hype_.replace(k, hype.get(k) + 1);
                            else
                                hype_.put(k, 1);
                        }
                    }
                    List<String> fields_ = new ArrayList<>(hype_.keySet());
                    fields_.sort((o1, o2) -> hype_.get(o2) - hype_.get(o1));
                    temp.put("fields", fields.subList(0, (3 > fields.size()? fields.size() : 3)));
                }
                rela.add(temp);
            }
            ret.put("relevantAuthor", (T) rela);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return ret;
    }

    @PostMapping("/ifCertificate")
    public Map<String, Object> checkCertification(@RequestBody Map<String, String> arg) {
        Map<String, Object> ret = new HashMap<>();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String userID = (String) session.getAttribute("userID");
        User user = userRepository.findByUserID(userID);
        if(user.getAuthorID() ==  null)
            ret.put("msg", "200");
        else{
            ret.put("msg", "201");
            ret.put("authorid", user.getAuthorID());
        }
        return ret;
    }

    @PostMapping("/applycertificate")
    public Map<String, Object> applyCertification(@RequestBody Map<String, String> arg) {
        Map<String, Object> ret = new HashMap<>();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String uid = (String) session.getAttribute("userID");
        String aid = arg.get("authorid");

        User user = userRepository.findByUserID(uid);
        User origin = userRepository.findByAuthorID(aid);
        if(user != null && origin == null) {
            userRepository.updateAuthorID(uid, aid);
            userRepository.updateUserIdentity(uid, 2);
            //userRepository.updateWorkCard(uid, "./static/certification/workcard" + uid + ".jpg");
            ret.put("msg", "200");
        }
        else {
            ret.put("msg", "201");
            if(user == null)
                ret.put("warning", "Unknown Error");
        }
        return ret;
    }

    @PostMapping("/identification")
    public Map<String, Object> identification(@RequestBody Map<String, String> arg) {
        Map<String, Object> ret = new HashMap<>();
        Application application = new Application();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String uid = (String) session.getAttribute("userID");

        User applier = userRepository.findByUserID(uid);
        if(applier.getUserIdentity() != 2) {
            ret.put("success", "false");
            ret.put("msg", "201");
            return ret;
        }
        String phone = arg.get("phoneNumber");
        String mail = arg.get("emailAddress");
        if(applier.getPhoneNumber() != null && !phone.equals(applier.getPhoneNumber())
                || applier.getEmailAddress() != null && !mail.equals(applier.getEmailAddress())) {
            ret.put("msg", "202");
        }

        if(arg.get("workCard") !=  null) {
            String image_base64 = arg.get("workCard").trim();
            int index = image_base64.indexOf("base64,") + 7;
            String newImage = image_base64.substring(index);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] imageBuf = decoder.decode(newImage);

            String path = "./static/certification";
            String name = path + "/workcard" + uid + ".jpg";
            File workCard = new File(name);
            File workCardPath = new File(path);
            if(!workCardPath.exists())
                workCardPath.mkdirs();
            try {
                if(!workCard.exists())
                    workCard.createNewFile();
                FileOutputStream out = new FileOutputStream(workCard);
                out.write(imageBuf);
                out.flush();
                out.close();
                applier.setRealName(workCard.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                ret.put("success", "false");
                ret.put("msg", "203");
                return ret;
            }
        }
        else {
            ret.put("success", "false");
            ret.put("msg", "203");
            return ret;
        }

        application.setApplicationTime(new java.sql.Date(new Date().getTime()));
        application.setType(4);
        application.setPhoneNumber1(phone);
        application.setEmailAddress(mail);
        application.setAuthorID(applier.getAuthorID());
        application.setWorkCard1(applier.getRealName());
        application.setUserID(uid);
        applicationRepository.save(application);
        ret.put("success", "true");
        if(null == ret.get("msg"))
            ret.put("msg", "200");
        return ret;
    }

    @PostMapping("/portalCert")
    public Map<String, Object> checkPortalCertification(@RequestBody Map<String, String> arg) {
        Map<String, Object> ret = new HashMap<>();
        String authorID = arg.get("authorid");
        User user = userRepository.findByAuthorID(authorID);
        if(user == null)
            ret.put("msg", "200");
        else
            ret.put("msg", "201");
        return  ret;
    }

    @PostMapping("/applyconflict")
    public Map<String, Object> applyConflict(@RequestBody Map<String, String> arg) {
        Map<String, Object> ret = new HashMap<>();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String uid = (String) session.getAttribute("userID");

        String emailAddress = arg.get("emailAddress");
        String phoneNumber = arg.get("phoneNumber2");
        String authorID = arg.get("authorid");

        User origin = userRepository.findByAuthorID(authorID);
        User applier = userRepository.findByUserID(uid);
        String absPath = null;
        if(arg.get("workCard2") !=  null) {
            String image_base64 = arg.get("workCard2").trim();
            int index = image_base64.indexOf("base64,") + 7;
            String newImage = image_base64.substring(index);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] imageBuf = decoder.decode(newImage);

            String path = "./static/certification";
            String name = path + "/workcard" + uid + ".jpg";
            File workCard = new File(name);
            File workCardPath = new File(path);
            if(!workCardPath.exists())
                workCardPath.mkdirs();
            try {
                if(!workCard.exists())
                    workCard.createNewFile();
                FileOutputStream out = new FileOutputStream(workCard);
                out.write(imageBuf);
                out.flush();
                out.close();
                absPath = workCardPath.getAbsolutePath();
                userRepository.updateWorkCard(uid, absPath);
            } catch (Exception e) {
                e.printStackTrace();
                ret.put("msg", "201");
                return ret;
            }
        }
        else {
            ret.put("msg", "201");
            return ret;
        }

        if(origin == null || applier == null) {
            ret.put("msg", "201");
            return ret;
        }
        Application application = new Application();
        application.setEmailAddress(emailAddress);
        application.setAuthorID(authorID);
        application.setUserID(uid);
        application.setPhoneNumber1(phoneNumber);
        application.setPhoneNumber2(origin.getPhoneNumber());
        application.setType(2);
        application.setWorkCard1(absPath);
        application.setWorkCard2(origin.getRealName());
        application.setApplicationTime(new java.sql.Date(new Date().getTime()));

        applicationRepository.save(application);

        ret.put("msg", "200");
        return ret;
    }

    // An aborted api
    @PostMapping("/addarticle")
    public Map<String, Object> addArticle(@RequestBody Map<String, Object> arg) {
        Map<String, Object> ret = new HashMap<>();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String username = (String) session.getAttribute("username");

        User current = userRepository.findByUsername(username);
        if(current == null || !current.getAuthorID().equals(arg.get("authorid"))) {
            ret.put("msg", "201");
            return ret;
        }
        Paper paper = new Paper();
        paper.setTitle((String) arg.get("title"));
        paper.setKeywords((ArrayList<String>) arg.get("keywords"));
        paper.setYear((int) arg.get("year"));
        paper.setAbstract_((String) arg.get("abstract"));
        Map<String, String> venue = new HashMap<>();
        venue.put("name", (String) arg.get("source"));
        List<String> authors = (List<String>) arg.get("name");
        ArrayList<Map<?,?>> authors_ = new ArrayList<>();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        SearchRequest searchRequest = new SearchRequest("paper");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("venue.name", arg.get("name")));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit sh = searchHits.getAt(0);
            Map<?,?> elem = sh.getSourceAsMap();
            Map<?,?> venue_ = (Map<?,?>) elem.get("venue");
            venue.put("id", (String) venue_.get("id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        paper.setVenue(venue);

        for(String name : authors) {
            List<Author> result = authorRepository.findAllByName(name);
            if(result.size() == 1) {
                Map<String, String> info = new HashMap<>();
                info.put("name", name);
                info.put("id", result.get(0).getId());
                authors_.add(info);
            }
            else {
                Map<String, String> info = new HashMap<>();
                info.put("name", name);
                info.put("id", null);
                authors_.add(info);
            }
        }
        paper.setAuthors(authors_);
        paperRepository.save(paper);
        ret.put("msg", "200");
        return ret;
    }

    @PostMapping("/ifShow")
    public Map<String, Object> hideOrShow(@RequestBody Map<String, String> arg) {
        Map<String, Object> ret = new HashMap<>();
        String essayID = arg.get("articleid");
        Optional<Paper> paper_ = paperRepository.findById(essayID);
        if(!paper_.isPresent()) {
            ret.put("msg", "201");
            return ret;
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String username = (String) session.getAttribute("username");

        User current = userRepository.findByUsername(username);
        if(current == null || !current.getAuthorID().equals(paper_.get().getAuthors().get(0).get("id")) || current.getUserIdentity() != 3) {
            ret.put("msg", "201");
            return ret;
        }
        //paperRepository.deleteById(essayID);
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("121.36.60.6", 9200, "http")));
        try{
            DeleteRequest request_ = new DeleteRequest("paper","_doc", essayID);
            DeleteResponse response = client.delete(request_, RequestOptions.DEFAULT);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            ret.put("msg", "201");
            return ret;
        }
        ret.put("msg", "200");
        return ret;
    }

    @PostMapping("/portalReport")
    public Map<String, Object> report(@RequestBody Map<String, String> arg) {
        Map<String, Object> ret = new HashMap<>();
        String aid = arg.get("authorID");
        String reason = arg.get("content");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String uid = (String) session.getAttribute("userID");
        if(uid == null) {
            ret.put("success", "false");
            ret.put("msg", "201");
            return ret;
        }
        User reporter = userRepository.findByUserID(uid);
        assert reporter != null;
        User reportee = userRepository.findByAuthorID(aid);
        if(reportee == null) {
            ret.put("success", "false");
            ret.put("msg", "202");
            return  ret;
        }
        Report report = new Report();
        report.setContent(reason);
        report.setUserID(uid);
        report.setReporteeID12(aid);
        report.setStatus(0);
        report.setType(2);
        reportRepository.save(report);

        ret.put("success", "true");
        ret.put("msg", "200");

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
