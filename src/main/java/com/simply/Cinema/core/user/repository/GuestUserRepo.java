package com.simply.Cinema.core.user.repository;

import com.simply.Cinema.core.user.entity.GuestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestUserRepo extends JpaRepository<GuestUser, Long> {
    Optional<GuestUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
