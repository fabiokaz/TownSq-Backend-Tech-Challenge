package com.kaz.townsq.repository;

import com.kaz.townsq.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsernameAndUserIdNot(String username, Long userId);

    Optional<User> findByUserId(Long userId);
}