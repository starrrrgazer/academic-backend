package com.example.community.dao;

import com.example.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Object> {
    List<Comment> findAllByUserID(String userID);
}
