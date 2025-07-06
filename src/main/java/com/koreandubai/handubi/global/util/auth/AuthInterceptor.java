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

                if (handlerMethod.getMethodAnnotation(AdminOnly.class) != null
                        || handlerMethod.getBeanType().isAnnotationPresent(AdminOnly.class)) {
                    checkAdmin(request.getSession());
                    return true;
                }

                if (handlerMethod.getMethodAnnotation(AuthRequired.class) != null
                        || handlerMethod.getBeanType().isAnnotationPresent(AuthRequired.class)) {
                    checkUserOrAdminWithIdMatch(request);
                }
                return true;
//                if (isNeedToAuth(handlerMethod)) {
//                    HttpSession session = request.getSession(false);
//                    if (session == null) throw new NoAuthorizationData();
//
//                    Long userIdBySession = getUserIdBySession(session);
//                    UserType role = (UserType) session.getAttribute(SessionKey.LOGIN_USER_ROLE);
//
//                    if (userIdBySession == null || role == null) {
//                        throw new NoAuthorizationData();
//                    }
//
//                    if (role == UserType.ADMIN) return true;
//
//                    String userIdByPath = getUserIdByPathVariable(request);
//                    if (!String.valueOf(userIdBySession).equals(userIdByPath)) {
//                        throw new UnauthorizedException();
//                    }
//                }
            }
            return true;
        } catch (NoAuthorizationData | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException(e);
        }
    }

    private void checkAdmin(HttpSession session) {
        if (session == null) throw new NoAuthorizationData();
        UserType role = (UserType) session.getAttribute(SessionKey.LOGIN_USER_ROLE);
        System.out.println(role);
        if (role != UserType.ADMIN) throw new UnauthorizedException();
    }

    private void checkUserOrAdminWithIdMatch(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (session == null) throw new NoAuthorizationData();

        Long sessId = (Long) session.getAttribute(SessionKey.LOGIN_USER_ID);
        UserType role = (UserType) session.getAttribute(SessionKey.LOGIN_USER_ROLE);
        if (sessId == null || role == null) throw new NoAuthorizationData();
        if (role == UserType.ADMIN) return;

        @SuppressWarnings("unchecked")
        Map<String, String> vars = (Map<String, String>)
                req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String pathId = vars == null ? null : vars.get("id");
        if (!String.valueOf(sessId).equals(pathId)) throw new UnauthorizedException();
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