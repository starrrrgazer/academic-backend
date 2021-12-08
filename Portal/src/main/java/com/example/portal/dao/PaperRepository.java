package com.example.portal.dao;

import com.example.portal.entity.AuthorList;
import com.example.portal.entity.Paper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PaperRepository extends ElasticsearchRepository<Paper, String> {
    List<Paper> findByAuthorListOrderByN_citationDesc(AuthorList al);
    List<Paper> finfByAuthorListOrderByYearDesc(AuthorList al);
}
