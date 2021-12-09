package com.example.literature.dao;

import com.example.literature.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Object> {

    User findByUsername(String username);

    User findByUserID(String userid);

    User findByUsernameAndPassword(String username,String password);
}
