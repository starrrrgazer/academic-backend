package com.example.management.mapper;

import com.example.management.Entity.Application;
import com.example.management.Entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.joda.time.DateTime;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    User getUserByuserId(String userID);
    int blockUser(String userID, Date unblockDate,String kind);
    int unblockUser(String userID,Date nowDate);
    User getUserByAuthorID(String authorId);
}
