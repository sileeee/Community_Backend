package com.koreandubai.handubi.service;

import com.koreandubai.handubi.controller.dto.AuthInfo;
import com.koreandubai.handubi.controller.dto.SignInRequestDto;
import com.koreandubai.handubi.domain.User;
import com.koreandubai.handubi.global.common.SessionKey;
import com.koreandubai.handubi.global.common.UserType;
import com.koreandubai.handubi.global.util.crypt.CryptoData;
import com.koreandubai.handubi.global.util.crypt.Encryptor;
import com.koreandubai.handubi.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class SessionLoginService implements LoginService{

    private final UserRepository userRepository;
    private final HttpSession httpSession;
    private final Encryptor encryptor;

    @Override
    public void login(SignInRequestDto dto, HttpServletRequest request) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("The email does not exist."));

        CryptoData cryptoData = CryptoData.WithSaltBuilder()
                .plainText(dto.getPassword())
                .salt(user.getSalt())
                .build();

        if (!encryptor.encrypt(cryptoData).equals(user.getPassword())) {
            throw new IllegalArgumentException("The password is incorrect.");
        }

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) oldSession.invalidate();

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute(SessionKey.LOGIN_USER_ID, user.getId());
        newSession.setAttribute(SessionKey.LOGIN_USER_ROLE, user.getUserType());
        newSession.setAttribute(SessionKey.LOGIN_USER_NAME, user.getName());
    }

    @Override
    public void logout(){
        httpSession.invalidate();
    }

    @Override
    public AuthInfo isLoggedIn(HttpServletRequest request){

        HttpSession session = request.getSession(false);
        if (session == null) {
            return AuthInfo.builder()
                    .isLoggedIn(false)
                    .build();
        }

        Long userId = (Long) session.getAttribute(SessionKey.LOGIN_USER_ID);
        UserType role = (UserType) session.getAttribute(SessionKey.LOGIN_USER_ROLE);
        String name = (String) session.getAttribute(SessionKey.LOGIN_USER_NAME);

        if (userId == null || role == null) {
            return AuthInfo.builder()
                    .isLoggedIn(false)
                    .build();
        }

        return AuthInfo.builder()
                .userId(userId)
                .name(name)
                .role(role)
                .isLoggedIn(true)
                .build();
    }
}
