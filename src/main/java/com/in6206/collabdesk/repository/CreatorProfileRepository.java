package com.in6206.collabdesk.repository;

import com.in6206.collabdesk.entity.CreatorProfile;
import com.in6206.collabdesk.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CreatorProfileRepository extends JpaRepository<CreatorProfile, Long> {

    Optional<CreatorProfile> findByUserId(Long userId);

    /** Directory search; any null parameter is ignored. */
    @Query("""
            select c from CreatorProfile c join fetch c.user u
            where u.enabled = true
              and (:platform is null or c.platform = :platform)
              and (:niche is null or lower(c.niche) like lower(concat('%', :niche, '%')))
              and (:maxRate is null or c.baseRate <= :maxRate)
            order by c.followerCount desc
            """)
    List<CreatorProfile> search(@Param("platform") Platform platform,
                                @Param("niche") String niche,
                                @Param("maxRate") BigDecimal maxRate);
}
