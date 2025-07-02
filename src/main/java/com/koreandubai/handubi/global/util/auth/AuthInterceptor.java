package com.koreandubai.handubi.global.util.auth;


import com.koreandubai.handubi.global.common.SessionKey;
import com.koreandubai.handubi.global.common.UserType;
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

/**
 * For endpoints annotated with @AuthRequired
 *   1) If there is no session, or the session lacks an ID/ROLE → respond with 401 Unauthorized
 *   2) If ROLE == ADMIN → allow
 *   3) For regular users → allow only when the {id} in the URL matches the session ID
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws UnauthorizedException {
        try {
            if (handler instanceof HandlerMethod handlerMethod) {
                if (isNeedToAuth(handlerMethod)) {
                    HttpSession session = request.getSession(false);
                    if (session == null) throw new NoAuthorizationData();

                    Long userIdBySession = getUserIdBySession(session);
                    UserType role = (UserType) session.getAttribute(SessionKey.LOGIN_USER_ROLE);

                    if (userIdBySession == null || role == null) {
                        throw new NoAuthorizationData();
                    }

                    if (role == UserType.ADMIN) return true;

                    String userIdByPath = getUserIdByPathVariable(request);
                    if (!String.valueOf(userIdBySession).equals(userIdByPath)) {
                        throw new UnauthorizedException();
                    }
                }
            }
            return true;
        } catch (NoAuthorizationData | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException(e);
        }
    }

    private boolean isNeedToAuth(HandlerMethod handler) {
        return handler.getMethodAnnotation(AuthRequired.class) != null
                || handler.getBeanType().isAnnotationPresent(AuthRequired.class);
    }

    public Long getUserIdBySession(HttpSession session) {

        Object idObj = session.getAttribute(SessionKey.LOGIN_USER_ID);
        if (idObj == null) throw new NoAuthorizationData();
        return (Long) idObj;
    }

    private String getUserIdByPathVariable(HttpServletRequest request) {

        Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (!(attr instanceof Map)) throw new NoAuthorizationData();

        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) attr;
        return Optional.ofNullable(pathVariables.get("id"))
                .orElseThrow(NoAuthorizationData::new);
    }
}