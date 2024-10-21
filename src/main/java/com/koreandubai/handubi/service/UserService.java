package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.SignUpRequestDto;
import com.koreandubai.handubi.controller.dto.UpdateUserInfoRequestDto;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.repository.UserRepository;
import com.koreandubai.handubi.global.util.CryptoData;
import com.koreandubai.handubi.global.util.Encryptor;
import com.koreandubai.handubi.global.util.SaltGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        String salt = SaltGenerator.generateSalt();
        CryptoData cryptoData = CryptoData.WithSaltBuilder()
                .plainText(dto.getPassword())
                .salt(salt)
                .build();
        String encryptedPassword = encryptor.encrypt(cryptoData);
        User user = dto.toEntity(salt, encryptedPassword);

        userRepository.save(user);
    }

    public boolean checkIsNameExist (String name) {
        return userRepository.findByName(name).isPresent();
    }

    public boolean checkIsEmailExist (String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void updateUserInfo(UpdateUserInfoRequestDto dto){

        if (checkIsNameExist(dto.getName())){
            throw new IllegalArgumentException("There is already a user with that name");
        }

        Optional<User> updateUser = userRepository.findByEmail(dto.getEmail());

        if(updateUser.isEmpty())
            throw new IllegalArgumentException("There isn't a user with that email");

        String salt = SaltGenerator.generateSalt();
        CryptoData cryptoData = CryptoData.WithSaltBuilder()
                .plainText(dto.getPassword())
                .salt(salt)
                .build();
        String encryptedPassword = encryptor.encrypt(cryptoData);

        updateUser.ifPresent(selectUser->{
            selectUser.setName(dto.getName());
            selectUser.setPassword(encryptedPassword);
            selectUser.setPhone(dto.getPhone());

            userRepository.save(selectUser);
        });
    }
}
