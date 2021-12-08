package com.example.management.Service;

import com.example.management.Entity.AuthorList;
import com.example.management.Entity.Paper;
import com.example.management.Entity.Venue;
import com.example.management.mapper.PaperMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaperService {

    @Autowired
    private PaperMapper paperMapper;

    public Map<String, Object> addPaper(Map<String,Object> map){
        Map<String, Object> returnObject = new HashMap<String, Object>();
        Map<String,Object> newPaperMap = new HashMap<>();
        try{
            //id
            String id = (String) map.get("id");
            Paper a = paperMapper.getPaperById(id);
            if (a!=null){
                returnObject.put("status","402");
                returnObject.put("result","文章已存在");
                return returnObject;
            }
            newPaperMap.put("id",id);
            //authors
            Map<String, Object> authors = (Map<String, Object>) map.get("authors");
            List<Map<String,Object>> authorList = new ArrayList<Map<String,Object>>();
            for (int i = 0; i < authors.size(); i++) {
                Map<String,Object> tnmp = (Map<String, Object>) authors.get("author"+(i+1));
                authorList.add(tnmp);
            }
            newPaperMap.put("authors",authorList);
            //title
            String title = (String) map.get("title");
            newPaperMap.put("title",title);
            //year
            Long year = (Long) map.get("year");
            newPaperMap.put("year",year);
            //n_citation
            Long n_citation = (Long) map.get("n_citation");
            newPaperMap.put("n_citation",n_citation);
            //url
            List<String> urls = new ArrayList<>();
            Map<String, Object> getUrls = (Map<String, Object>) map.get("urls");
            for (int i = 0; i < getUrls.size(); i++) {
                urls.add((String) getUrls.get("url" + (i + 1)));
            }
            newPaperMap.put("url",urls);
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
            List<String> keywords = new ArrayList<>();
            Map<String, Object> getKeywords = (Map<String, Object>) map.get("keywords");
            for (int i = 0; i < getKeywords.size(); i++) {
                keywords.add((String) getUrls.get("keywords" + (i + 1)));
            }
            newPaperMap.put("keywords",keywords);
            //venue
            Map<String, Object> getVenue = (Map<String, Object>) map.get("venue");
            Venue venue = new Venue((String) getVenue.get("id"), (String) getVenue.get("raw"));
            newPaperMap.put("venue",venue);

            Paper newPaper = new Paper(newPaperMap);
            paperMapper.addPaper(newPaper);


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
        Paper a = paperMapper.getPaperById(id);
        if (a==null){
            returnObject.put("status","403");
            returnObject.put("result","文章不存在");
            return returnObject;
        }
        paperMapper.deletePaper(id);
        returnObject.put("status","200");
        returnObject.put("result","删除成功");
        return returnObject;
    }

    public Map<String,Object> getArticleList(Map<String,Object> map){
        return null;
    }
}
