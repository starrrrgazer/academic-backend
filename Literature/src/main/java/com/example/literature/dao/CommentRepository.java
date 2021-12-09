package com.example.literature.dao;

import com.example.literature.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Object> {
    List<Comment> findAllByToID(String toID);
}
