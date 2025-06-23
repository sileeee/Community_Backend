package com.koreandubai.handubi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_point")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder
    public UserPoint(Long id, Long userId, Integer totalPoints) {
        this.id = id;
        this.userId = userId;
        this.totalPoints = totalPoints;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

