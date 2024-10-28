package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.SignInRequestDto;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.SessionKey;
import com.koreandubai.handubi.global.util.crypt.CryptoData;
import com.koreandubai.handubi.global.util.crypt.Encryptor;
import com.koreandubai.handubi.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class SessionLoginService implements LoginService{

    private final UserRepository userRepository;
    private final HttpSession httpSession;
    private final Encryptor encryptor;

    @Override
    public void login(SignInRequestDto dto){

        if (userRepository.findByEmail(dto.getEmail()).isEmpty()){
            throw new IllegalArgumentException("The email does not exist.");
        }
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        CryptoData cryptoData = CryptoData.WithSaltBuilder()
                .plainText(dto.getPassword())
                .salt(user.get().getSalt())
                .build();
        String encryptedPassword = encryptor.encrypt(cryptoData);

        if(!encryptedPassword.equals(user.get().getPassword())){
            throw new IllegalArgumentException("The password is incorrect.");
        }
        httpSession.setAttribute(SessionKey.LOGIN_USER_ID, user.get().getId());
    }

    @Override
    public void logout(){

        httpSession.removeAttribute(SessionKey.LOGIN_USER_ID);
    }
}
