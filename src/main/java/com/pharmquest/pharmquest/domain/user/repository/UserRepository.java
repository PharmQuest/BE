package com.pharmquest.pharmquest.domain.user.repository;

import com.pharmquest.pharmquest.domain.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByName(String userName);
}
