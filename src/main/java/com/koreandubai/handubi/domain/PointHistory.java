package com.koreandubai.handubi.domain;

import com.koreandubai.handubi.global.common.ActionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_history")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "action_type")
    private ActionType actionType;

    private Integer points;

    @Column(name = "reference_post_id")
    private Long referencePostId;

    @Column(name = "reference_comment_id")
    private Long referenceCommentId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public PointHistory(Long userId, ActionType actionType, Integer points, Long referencePostId, Long referenceCommentId) {
        this.userId = userId;
        this.actionType = actionType;
        this.points = points;
        this.referencePostId = referencePostId;
        this.referenceCommentId = referenceCommentId;
        this.createdAt = LocalDateTime.now();
    }
}

