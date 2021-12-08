package com.example.portal.dao;

import com.example.portal.entity.AuthorList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AuthorListRepository extends ElasticsearchRepository {
    AuthorList findById(String id);
}
