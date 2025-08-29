package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.ServiceItem;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
}
