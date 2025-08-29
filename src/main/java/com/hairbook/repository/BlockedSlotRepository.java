package com.hairbook.repository;

import com.hairbook.entity.BlockedSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BlockedSlotRepository extends JpaRepository<BlockedSlot, Long> {
    List<BlockedSlot> findByStartAtLessThanEqualAndEndAtGreaterThanEqual(
            LocalDateTime start, LocalDateTime end);
}
