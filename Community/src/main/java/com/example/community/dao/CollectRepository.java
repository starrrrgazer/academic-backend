package com.example.community.dao;

import com.example.community.entity.Collect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectRepository extends JpaRepository<Collect,Object> {
}
