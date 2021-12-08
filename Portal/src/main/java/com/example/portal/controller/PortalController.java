package com.example.portal.controller;

import com.example.portal.dao.AuthorListRepository;
import com.example.portal.dao.AuthorRepository;
import com.example.portal.dao.PaperRepository;
import com.example.portal.entity.Author;
import com.example.portal.entity.AuthorList;
import com.example.portal.entity.Paper;
import com.mysql.cj.xdevapi.JsonArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true",maxAge =
        3600)
@RestController
public class PortalController {
    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    AuthorListRepository authorListRepository;

    @Autowired
    PaperRepository paperRepository;


    @PostMapping("/getUserInfo")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        Map<String, Object> ret = new HashMap<>();
        String id = request.getParameter("userID");
        Optional<Author> query = authorRepository.findById(id);
        if(query.isPresent()) {
            Author author = query.get();
            ret.put("success", "true");
            ret.put("citeEssayNum", "" + author.getN_citation());
            ret.put("coreEaasyNum", "" + author.getN_pubs());
            ret.put("avgciteEssayNum", "" + author.getN_citation()/author.getN_pubs());
            ret.put("userName", author.getName());
            ret.put("unit", Arrays.asList(author.getOrgs().split(",")));

            String aid = author.getId();
            AuthorList al = authorListRepository.findById(aid);
            List<Paper> paperList = paperRepository.findByAuthorListOrderByN_citationDesc(al);
            List<Essay> essayList = new LinkedList<>();
            for(Paper p : paperList) {
                List<AuthorList> all = p.getAuthors();
                List<String> authors = new LinkedList<>();
                for(AuthorList cur: all)
                    authors.add(cur.getName());
                essayList.add(new Essay(p.getTitle(), authors, p.getAbstracts(), p.getYear(), p.getVenues().getRaw(), p.getKeywords()));
            }
            ret.put("essayList", essayList);

        }
        else {
            ret.put("success", "false");
            ret.put("msg", "找不到该作者");
        }
        return ret;
    }

    @PostMapping("/getHighCiteList/{aid}")
    public Map<String, Object> getHighCiteList(HttpServletRequest request, @PathVariable("aid") String aid) {
        Map<String, Object> ret = new HashMap<>();
        AuthorList al = authorListRepository.findById(aid);
        if(al != null) {
            ret.put("success", "true");
            List<Paper> paperList = paperRepository.findByAuthorListOrderByN_citationDesc(al);
            List<Essay> essayList = new LinkedList<>();
            for(Paper p : paperList) {
                List<AuthorList> all = p.getAuthors();
                List<String> authors = new LinkedList<>();
                for(AuthorList cur: all)
                    authors.add(cur.getName());
                essayList.add(new Essay(p.getTitle(), authors, p.getAbstracts(), p.getYear(), p.getVenues().getRaw(), p.getKeywords()));
            }
            ret.put("essayList", essayList);
        }
        else {
            ret.put("success", "false");
            ret.put("msg", "找不到该作者");
        }
        return ret;
    }

    @PostMapping("/getNewPostList/{aid}")
    public Map<String, Object> getNewPostList(HttpServletRequest request, @PathVariable("aid") String aid) {
        Map<String, Object> ret = new HashMap<>();
        AuthorList al = authorListRepository.findById(aid);
        if(al != null) {
            ret.put("success", "true");
            List<Paper> paperList = paperRepository.finfByAuthorListOrderByYearDesc(al);
            List<Essay> essayList = new LinkedList<>();
            for(Paper p : paperList) {
                List<AuthorList> all = p.getAuthors();
                List<String> authors = new LinkedList<>();
                for(AuthorList cur: all)
                    authors.add(cur.getName());
                essayList.add(new Essay(p.getTitle(), authors, p.getAbstracts(), p.getYear(), p.getVenues().getRaw(), p.getKeywords()));
            }
            ret.put("essayList", essayList);
        }
        else {
            ret.put("success", "false");
            ret.put("msg", "找不到该作者");
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
}