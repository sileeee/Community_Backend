package com.koreandubai.handubi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.koreandubai.handubi.global.common.ActionCode;
import jakarta.persistence.*;
import lombok.Getter;


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "action_type")
@Getter
public class ActionType {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_code", nullable = false, unique = true, length = 32)
    private ActionCode actionCode;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(name = "point", nullable = false)
    private Integer defaultPoint;
}
