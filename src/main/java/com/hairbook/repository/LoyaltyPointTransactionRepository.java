package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.LoyaltyPointTransaction;

import java.util.List;

@Repository
public interface LoyaltyPointTransactionRepository extends JpaRepository<LoyaltyPointTransaction, Long> {
    List<LoyaltyPointTransaction> findByUser_IdOrderByTransactionDateDesc(Long userId);
}
