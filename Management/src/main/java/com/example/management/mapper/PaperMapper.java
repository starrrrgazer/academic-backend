package com.example.management.mapper;

import com.example.management.Entity.Paper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaperMapper {
    int addPaper(Paper paper);
    Paper getPaperById(String id);
    int deletePaper(String id);
    List<Paper> getArticleList();
}
