package com.weatheralert.repository;

import com.weatheralert.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    static void remove(String id) {
    }

    Optional<User> findByEmail(String email);
}
