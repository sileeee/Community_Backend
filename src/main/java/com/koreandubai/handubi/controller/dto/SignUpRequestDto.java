package com.koreandubai.handubi.controller.dto;

import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;


@Getter
public class SignUpRequestDto {

    @NotBlank(message = "You should enter a name.")
    private final String name;

    @NotBlank(message = "You should enter a email.")
    @Email(message = "You should ensure email format")
    private final String email;

    @NotBlank(message = "You should enter a password.")
    private final String password;

    @NotBlank(message = "You should enter a phone number.")
    @Pattern(regexp = "[0-9]{10,11}", message = "You should enter a phone number with 10-11 digits.")
    private final String phone;

    private final LocalDate birthday;

    @Builder
    public SignUpRequestDto(String name, String email, String password, String phone, LocalDate birthday) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthday = birthday;
    }

    public User toEntity(String salt, String encryptedPassword) {
        return User.builder()
                .name(name)
                .email(email)
                .salt(salt)
                .password(encryptedPassword)
                .phone(phone)
                .birthday(birthday)
                .userType(UserType.USER)
                .build();
    }
}
