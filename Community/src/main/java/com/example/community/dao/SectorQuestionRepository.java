package com.example.community.dao;

import com.example.community.entity.SectorQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorQuestionRepository extends JpaRepository<SectorQuestion,Object> {
}
