package com.example.management.mapper;

import com.example.management.Entity.Paper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaperMapper {
    int addPaper(Paper paper);
    Paper getPaperById(String id);
    int deletePaper(String id);
}
