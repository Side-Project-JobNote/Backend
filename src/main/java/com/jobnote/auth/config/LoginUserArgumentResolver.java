package com.jobnote.auth.config;

import com.jobnote.auth.security.dto.CustomUserDetails;
import com.jobnote.global.exception.JobNoteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.jobnote.global.common.ResponseCode.UNAUTHORIZED;

@Slf4j
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isCustomUserDetailsClass = CustomUserDetails.class.equals(parameter.getParameterType());

        return isLoginUserAnnotation && isCustomUserDetailsClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new JobNoteException(UNAUTHORIZED);
        }

        if (authentication.getPrincipal().equals("anonymousUser")) {
            log.error("principal is anonymous.");
            throw new JobNoteException(UNAUTHORIZED);
        }

        return authentication.getPrincipal();
    }
}
