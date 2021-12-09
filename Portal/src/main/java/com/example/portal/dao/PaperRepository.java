package com.example.portal.dao;

import com.example.portal.entity.AuthorList;
import com.example.portal.entity.Paper;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


import java.util.List;

public interface PaperRepository extends ElasticsearchRepository<Paper, String> {
    @Query("{\"sort\" : [{ \"n_citation\" : {\"order\" : \"desc\"} }],\"bool\" : {\"must\" : {\"field\" : {\"authors\" : true}}}}")
    List<Paper> findAllByAuthorsOrderByNCitationDesc(AuthorList al);

    List<Paper> findAllByAuthorsOrderByYearDesc(AuthorList al);
}
