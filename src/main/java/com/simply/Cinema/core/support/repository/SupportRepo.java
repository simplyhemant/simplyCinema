package com.simply.Cinema.core.support.repository;

import com.simply.Cinema.core.support.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportRepo extends JpaRepository<Support, Long> {
    List<Support> findByUserId(Long userId);
    List<Support> findByUserRole(String userRole);
    List<Support> findByStatus(String status);
}
