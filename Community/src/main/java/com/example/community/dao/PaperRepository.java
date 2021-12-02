package com.example.community.dao;

import com.example.community.entity.Paper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends ElasticsearchRepository<Paper,Object> {
    List<Paper> findByAbstractsLike(String abstracts);
}
