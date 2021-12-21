package com.example.portal.dao;

import com.example.portal.entity.Paper;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PaperRepository extends ElasticsearchRepository<Paper, String> {
    Optional<Paper> findById(String id);

    @Transactional
    void deleteById(String id);

    void refresh();
}
