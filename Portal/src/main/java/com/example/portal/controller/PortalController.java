package com.example.portal.controller;

import com.example.portal.dao.AuthorListRepository;
import com.example.portal.dao.AuthorRepository;
import com.example.portal.dao.PaperRepository;
import com.example.portal.entity.Author;
import com.example.portal.entity.AuthorList;
import com.example.portal.entity.Paper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public <T extends Object> Map<String, T> getUserInfo(@RequestBody Map<String, String> remap) {
        Map<String, T> ret = new HashMap<>();
        String id = remap.get("userID");
        Optional<Author> query = authorRepository.findById(id);
        if(query.isPresent()) {
            Author author = query.get();
            ret.put("success", (T) "true");
            ret.put("citeEssayNum", (T) ("" + author.getN_citation()));
            ret.put("coreEaasyNum", (T) ("" + author.getN_pubs()));
            ret.put("avgciteEssayNum", (T) ("" + author.getN_citation()/author.getN_pubs()));
            ret.put("userName", (T) author.getName());
            ret.put("unit", (T) Arrays.asList(author.getOrgs().split(",")));

            String aid = author.getId();
            AuthorList al = authorListRepository.findById(aid).get();
            List<Paper> paperList = paperRepository.findAllByAuthorsOrderByNCitationDesc(al);
            List<Essay> essayList = new LinkedList<>();
            for(Paper p : paperList) {
                List<AuthorList> all = p.getAuthors();
                List<String> authors = new LinkedList<>();
                for(AuthorList cur: all)
                    authors.add(cur.getName());
                essayList.add(new Essay(p.getTitle(), authors, p.getAbstracts(), p.getYear(), p.getVenues().getRaw(), p.getKeywords()));
            }
            ret.put("essayList", (T) essayList);

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
        AuthorList al = authorListRepository.findById(aid).get();
        if(al != null) {
            ret.put("success", (T) "true");
            List<Paper> paperList = paperRepository.findAllByAuthorsOrderByNCitationDesc(al);
            List<Essay> essayList = new LinkedList<>();
            for(Paper p : paperList) {
                List<AuthorList> all = p.getAuthors();
                List<String> authors = new LinkedList<>();
                for(AuthorList cur: all)
                    authors.add(cur.getName());
                essayList.add(new Essay(p.getTitle(), authors, p.getAbstracts(), p.getYear(), p.getVenues().getRaw(), p.getKeywords()));
            }
            ret.put("essayList", (T) essayList);
        }
        else {
            ret.put("success", (T) "false");
            ret.put("msg", (T) "找不到该作者");
        }
        return ret;
    }

    @PostMapping("/getNewPostList/{aid}")
    public <T extends Object> Map<String, T> getNewPostList(HttpServletRequest request, @PathVariable("aid") String aid) {
        Map<String, T> ret = new HashMap<>();
        AuthorList al = authorListRepository.findById(aid).get();
        if(al != null) {
            ret.put("success", (T) "true");
            List<Paper> paperList = paperRepository.findAllByAuthorsOrderByYearDesc(al);
            List<Essay> essayList = new LinkedList<>();
            for(Paper p : paperList) {
                List<AuthorList> all = p.getAuthors();
                List<String> authors = new LinkedList<>();
                for(AuthorList cur: all)
                    authors.add(cur.getName());
                essayList.add(new Essay(p.getTitle(), authors, p.getAbstracts(), p.getYear(), p.getVenues().getRaw(), p.getKeywords()));
            }
            ret.put("essayList", (T) essayList);
        }
        else {
            ret.put("success", (T) "false");
            ret.put("msg", (T) "找不到该作者");
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