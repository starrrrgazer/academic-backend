package com.example.community.dao;

import com.example.community.entity.UserTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTagRepository extends JpaRepository<UserTags,Object> {
    List<UserTags> findAllByUserID(String userID);
    int deleteAllByUserID(String userID);
}
