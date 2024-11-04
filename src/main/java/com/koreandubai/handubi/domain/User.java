package com.koreandubai.handubi.domain;

import com.koreandubai.handubi.global.common.UserType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String salt;
    private String phone;
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "user_type")
    private UserType userType;

    @Builder
    public User(Long id, String name, String email, String salt, String password, String phone, LocalDate birthday, UserType userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.salt = salt;
        this.password = password;
        this.phone = phone;
        this.birthday = birthday;
        this.userType = userType;
    }

    protected User() {

    }
}

