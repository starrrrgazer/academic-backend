package com.example.portal.dao;

import com.example.portal.entity.AuthorList;
import com.example.portal.entity.Paper;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


import java.util.List;

public interface PaperRepository extends ElasticsearchRepository<Paper, String> {
    @Query("{\"bool\":{\"must\":[{\"nested\":{\"path\":\"authors\",\"query\":{\"bool\":{\"must\":[{\"match\":{\"authors.id\":\"?\"}}]}}}}]}}")
    List<Paper> findAllByAuthorOrderByNCitationDesc(String id);

    @Query("{\"bool\":{\"must\":[{\"nested\":{\"path\":\"authors\",\"query\":{\"bool\":{\"must\":[{\"match\":{\"authors.id\":\"?\"}}]}}}}]}}")
    List<Paper> findAllByAuthorOrderByYearDesc(String id);
}
