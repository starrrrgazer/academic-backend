package com.example.literature.dao;

import com.example.literature.entity.Paper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends ElasticsearchRepository<Paper,Object> {

}
