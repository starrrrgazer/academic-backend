package com.example.management.mapper;

import com.example.management.Entity.Application;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApplicationMapper {
    int setAuthorUser(String AuthorId,String userID);
    Application getApplicationByAuthorID(String authorId);
    List<Application> getApplyList();
    void acceptApply(String applyId,String reason);
    Application getApplicationByID(String applicationID);
    void rejectApply(String applyId,String reason);
}
