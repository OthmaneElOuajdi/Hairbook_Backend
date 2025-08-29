package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.phone LIKE :phonePrefix%")
    List<User> findByPhoneStartingWith(@Param("phonePrefix") String phonePrefix);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByCreatedAtAfter(LocalDateTime date);

    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :cutoffDate OR u.lastLoginAt IS NULL")
    List<User> findInactiveUsersSince(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Query("SELECT COUNT(u) FROM User u WHERE u.phone LIKE '+32%' OR u.phone LIKE '0%'")
    long countBelgianUsers();
}
