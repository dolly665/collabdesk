package com.in6206.collabdesk.repository;

import com.in6206.collabdesk.entity.BrandProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandProfileRepository extends JpaRepository<BrandProfile, Long> {
    Optional<BrandProfile> findByUserId(Long userId);
}
