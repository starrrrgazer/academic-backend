package com.example.management.Service;

import com.example.management.Entity.AuthorList;
import com.example.management.Entity.Paper;
import com.example.management.Entity.Venue;
import com.example.management.mapper.PaperMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaperService {

    @Autowired
    private PaperMapper paperMapper;

    public Map<String, Object> addPaper(Map<String,Object> map){
        Map<String, Object> returnObject = new HashMap<String, Object>();
//        Map<String,Object> newPaperMap = new HashMap<>();
//        try{
//            //id
//            String id = (String) map.get("id");
//            Paper a = paperMapper.getPaperById(id);
//            if (a!=null){
//                returnObject.put("status","402");
//                returnObject.put("result","文章已存在");
//                return returnObject;
//            }
//            newPaperMap.put("id",id);
//            //authors
//            List<Map<String, Object>> authors = Collections.singletonList((Map<String, Object>) map.get("authors"));
//            List<AuthorList> authorList = new ArrayList<>();
//            for (int i = 0; i < authors.size(); i++) {
//                AuthorList tmp = new AuthorList(authors.get(i));
//                authorList.add(tmp);
//            }
//            newPaperMap.put("authors",authorList);
//            //title
//            String title = (String) map.get("title");
//            newPaperMap.put("title",title);
//            //year
//            Long year = (Long) map.get("year");
//            newPaperMap.put("year",year);
//            //n_citation
//            Long n_citation = (Long) map.get("n_citation");
//            newPaperMap.put("n_citation",n_citation);
//            //url
//            List<String> getUrls = Collections.singletonList((String) map.get("url"));
//            newPaperMap.put("url",getUrls);
//            //doi
//            String doi = (String) map.get("doi");
//            newPaperMap.put("doi",doi);
//            //page_start
//            newPaperMap.put("page_start", map.get("page_start"));
//            //page_end
//            newPaperMap.put("page_end", map.get("page_end"));
//            //abstracts
//            String abstracts = (String) map.get("abstracts");
//            newPaperMap.put("abstract",abstracts);
//            //issue
//            newPaperMap.put("issue",(String)map.get("issue"));
//            //volume
//            newPaperMap.put("volume",(String)map.get("volume"));
//            //issn
//            newPaperMap.put("issn",(String)map.get("issn"));
//            //isbn
//            newPaperMap.put("isbn",(String)map.get("isbn"));
//            //pdf
//            newPaperMap.put("pdf",(String)map.get("pdf"));
//            //keywords
//            List<String> keywords = (List<String>) map.get("keywords");
//            newPaperMap.put("keywords",keywords);
//            //venue
//            List<Map<String, Object>> getVenue = Collections.singletonList((Map<String, Object>) map.get("venue"));
//            List<Venue> m = new ArrayList<>();
//            for (int i = 0;i<getVenue.size();i++){
//                Venue venue = new Venue((String) getVenue.get(i).get("id"), (String) getVenue.get(i).get("raw"));
//                m.add(venue);
//            }
//            newPaperMap.put("venue",m);
//
//            Paper newPaper = new Paper(newPaperMap);
//            paperMapper.addPaper(newPaper);
//
//
//        } catch (Exception e) {
//            returnObject.put("status","401");
//            returnObject.put("result","未知错误");
//            return returnObject;
//        }

        returnObject.put("status","200");
        returnObject.put( "result","添加成功");
        return returnObject;
    }

    public Map<String,Object> deletePaper(Map<String,Object> map){
        String id = (String) map.get("id");
        Map<String,Object> returnObject = new HashMap<>();
//        Paper a = paperMapper.getPaperById(id);
//        if (a==null){
//            returnObject.put("status","403");
//            returnObject.put("result","文章不存在");
//            return returnObject;
//        }
//        paperMapper.deletePaper(id);
        returnObject.put("status","200");
        returnObject.put("result","删除成功");
        return returnObject;
    }

    public Map<String,Object> getArticleList(){
        Map<String,Object> returnObject = new HashMap<>();
        List<Map<String,Object>> papers = new ArrayList<>();
        for (int i = 0;i < 245;i++){
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("id","12dd");
            tmp.put("title","AAA");
            tmp.put("authors","xuhuibin");
            tmp.put("year","2021");
            String[] h = {"saa","ccc","aaa"};
            tmp.put("keyword",h);
            tmp.put("abstract","asssa");
            tmp.put("doi","222111");
            tmp.put("pdf","qwueqaq");
            papers.add(tmp);
        }
//        try{
//            List<Paper> tmpPaperList = paperMapper.getArticleList();
//            for (Paper tmpPaper : tmpPaperList){
//                Map<String,Object> tmp = new HashMap<>();
//                tmp.put("id",tmpPaper.getId());
//                tmp.put("title",tmpPaper.getTitle());
//                tmp.put("authors", tmpPaper.getAuthors());
//                tmp.put("year",tmpPaper.getYear());
//                tmp.put("keyword",tmpPaper.getKeywords());
//                tmp.put("abstract",tmpPaper.getAbstracts());
//                tmp.put("doi",tmpPaper.getDoi());
//                tmp.put("pdf",tmpPaper.getPdf());
//                papers.add(tmp);
//            }
//        } catch (Exception e) {
//            returnObject.put("status","401");
//            returnObject.put("result","未知错误");
//            return returnObject;
//        }
        returnObject.put("status","200");
        returnObject.put("result","处理成功");
        returnObject.put("articleList",papers);
        return returnObject;
    }
}
