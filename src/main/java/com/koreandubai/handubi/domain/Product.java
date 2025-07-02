package com.koreandubai.handubi.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int pointPrice;

    private int stockQty;

    private boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime deadline;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
