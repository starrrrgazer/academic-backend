package com.example.portal.dao;

import com.example.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmailAddress(String emailAdress);
    User findByUserID(String uid);
    User findByPhoneNumber(String phoneNum);

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
}
