package com.example.management.mapper;

import com.example.management.Entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> getCommentList();
    void deleteComment(String commentId);
    Comment getCommentById(String commentId);
}
