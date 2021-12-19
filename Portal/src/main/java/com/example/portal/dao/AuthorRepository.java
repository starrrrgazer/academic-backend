package com.example.portal.dao;

import com.example.portal.entity.Author;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends ElasticsearchRepository<Author, String> {
    Optional<Author> findById(String id);
    List<Author> findAllByName(String name);
}
