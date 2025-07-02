package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.controller.dto.UserPointSummaryDto;
import com.koreandubai.handubi.domain.UserPoint;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    Optional<UserPoint> findByUserId(Long userId);

    @Query("""
      SELECT new com.koreandubai.handubi.controller.dto.UserPointSummaryDto(u.id, u.email, u.name, up.totalPoints, up.updatedAt)
      FROM UserPoint up
      JOIN User u ON up.userId = u.id
      WHERE (:q IS NULL OR u.name LIKE %:q% OR CAST(u.id AS string) LIKE %:q%)
      ORDER BY up.updatedAt DESC
    """)
    List<UserPointSummaryDto> searchWithUser(@Param("q") String query);
}
