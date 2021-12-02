package com.example.community.dao;

import com.example.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Object> {

    User findByUsername(String username);

    User findByUserID(String userid);
}
