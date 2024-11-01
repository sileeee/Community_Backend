package com.koreandubai.handubi.global.util.auth;


import com.koreandubai.handubi.global.common.SessionKey;
import com.koreandubai.handubi.global.exception.NoAuthorizationData;
import com.koreandubai.handubi.global.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import java.util.Map;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws UnauthorizedException {
        try {
            if (handler instanceof HandlerMethod handlerMethod) {
                if (isNeedToAuth(handlerMethod)) {
                    String userIdBySession = getUserIdBySession(request);
                    String userIdByPath = getUserIdByPathVariable(request);
                    if (!userIdBySession.equals(userIdByPath)) {
                        throw new UnauthorizedException();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            throw new UnauthorizedException(e);
        }
    }

    private boolean isNeedToAuth(HandlerMethod handler) {
        if (handler.getMethodAnnotation(AuthRequired.class) == null) {
            return false;
        }
        return true;
    }

    public String getUserIdBySession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return Optional.ofNullable(session.getAttribute(SessionKey.LOGIN_USER_ID))
                .map(Object::toString)
                .orElseThrow(NoAuthorizationData::new);
    }

    private String getUserIdByPathVariable(HttpServletRequest request) {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return Optional.ofNullable(pathVariables.get("id"))
                .orElseThrow(NoAuthorizationData::new);
    }
}