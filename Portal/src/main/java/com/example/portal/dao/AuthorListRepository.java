package com.example.portal.dao;

import com.example.portal.entity.AuthorList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface AuthorListRepository extends ElasticsearchRepository<AuthorList, String> {
    Optional<AuthorList> findById(String id);
}
