package com.example.management.mapper;

import com.example.management.Entity.Application;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationMapper {
    int setAuthorUser(String AuthorId,String userID);
    Application getApplicationByAuthorID(String authorId);
}
