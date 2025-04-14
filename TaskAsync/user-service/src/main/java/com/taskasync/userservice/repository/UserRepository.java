package com.taskasync.userservice.repository;

import com.taskasync.userservice.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakSubjectId(String keycloakSubjectId);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByKeycloakSubjectId(String keycloakSubjectId);




}