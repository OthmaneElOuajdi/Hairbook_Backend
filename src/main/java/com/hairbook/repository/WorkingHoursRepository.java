package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.WorkingHours;

import java.time.DayOfWeek;
import java.util.Optional;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

    Optional<WorkingHours> findByDayOfWeek(DayOfWeek dayOfWeek);
}
