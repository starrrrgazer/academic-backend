package com.example.portal.dao;

import com.example.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    User findByEmailAddress(String emailAddress);
    User findByUserID(String uid);
    User findByPhoneNumber(String phoneNum);
    User findByAuthorID(String authorID);

    @Transactional
    @Modifying
    @Query(value = "update User set password=?2 where username=?1", nativeQuery = true)
    void updatePassword(String username, String password);

    @Transactional
    @Modifying
    @Query(value = "update User set username=?2 where username=?1", nativeQuery = true)
    void updateUsername(String newly, String origin);

    @Transactional
    @Modifying
    @Query(value = "update User set phoneNumber=?2 where username=?1",nativeQuery = true)
    void updatePhoneNumber(String username, String phoneNumber);

    @Transactional
    @Modifying
    @Query(value = "update User set authorID=?2 where userID=?1", nativeQuery = true)
    void updateAuthorID(String userID, String authorID);

    @Transactional
    @Modifying
    @Query(value = "update User set organization=?2 where username=?1", nativeQuery = true)
    void updateOrganization(String username, String organization);
}
