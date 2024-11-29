package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.SecureUserInfo;
import com.koreandubai.handubi.controller.dto.SignUpRequestDto;
import com.koreandubai.handubi.controller.dto.UpdateUserInfoRequestDto;
import com.koreandubai.handubi.domain.EncryptedPassword;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.SessionKey;
import com.koreandubai.handubi.global.exception.UnauthorizedException;
import com.koreandubai.handubi.repository.UserRepository;
import com.koreandubai.handubi.global.util.crypt.CryptoData;
import com.koreandubai.handubi.global.util.crypt.Encryptor;
import com.koreandubai.handubi.global.util.crypt.SaltGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Qualifier("sha256Encryptor")
    private final Encryptor encryptor;

    @Transactional
    public void join(SignUpRequestDto dto){

        if (checkIsNameExist(dto.getName())){
            throw new IllegalArgumentException("There is already a user with that name");
        }

        if (checkIsEmailExist(dto.getEmail())) {
            throw new IllegalArgumentException("There is already a user with that email");
        }

        EncryptedPassword pw = encryptPasswordWithSalt(dto.getPassword());
        User user = dto.toEntity(pw.getSalt(), pw.getPassword());

        userRepository.save(user);
    }

    public boolean checkIsNameExist (String name) {
        return userRepository.findByName(name).isPresent();
    }

    public boolean checkIsEmailExist (String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private EncryptedPassword encryptPasswordWithSalt(String plainPassword) {
        String salt = SaltGenerator.generateSalt();
        CryptoData cryptoData = CryptoData.WithSaltBuilder()
                .plainText(plainPassword)
                .salt(salt)
                .build();
        String encryptedPassword = encryptor.encrypt(cryptoData);
        return EncryptedPassword.builder()
                .salt(salt)
                .password(encryptedPassword)
                .build();
    }

    @Transactional
    public void updateUserInfo(long userId, UpdateUserInfoRequestDto dto) {

        User updateUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (!dto.getName().isEmpty() && !Objects.equals(updateUser.getName(), dto.getName())) {
            if (checkIsNameExist(dto.getName())) {
                throw new IllegalArgumentException("There is already a user with that name");
            }
            updateUser.setName(dto.getName());
        }

        if (!dto.getPassword().isEmpty()) {
            EncryptedPassword pw = encryptPasswordWithSalt(dto.getPassword());
            updateUser.setSalt(pw.getSalt());
            updateUser.setPassword(pw.getPassword());
        }

        if (!dto.getPhone().isEmpty()) {
            updateUser.setPhone(dto.getPhone());
        }

        userRepository.save(updateUser);
    }


    public Long getUserIdFromSession(HttpServletRequest request) {

        HttpSession session = request.getSession();
        return (Long) Optional.ofNullable(session.getAttribute(SessionKey.LOGIN_USER_ID))
                .orElseThrow(UnauthorizedException::new);
    }

    public SecureUserInfo getSecureUserInfo(long userId) {

        Optional<User> user = userRepository.findById(userId);

        return SecureUserInfo.builder()
                .id(userId)
                .name(user.get().getName())
                .email(user.get().getEmail())
                .phone(user.get().getPhone())
                .build();
    }
}
