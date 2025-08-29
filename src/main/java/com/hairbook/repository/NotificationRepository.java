package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.hairbook.entity.Notification;
import com.hairbook.entity.NotificationStatus;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByUser_IdOrderBySentAtDesc(Long userId);
}
