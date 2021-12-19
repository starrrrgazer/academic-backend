package com.example.portal.dao;

import com.example.portal.entity.Paper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PaperRepository extends ElasticsearchRepository<Paper, String> {
}
