package jungle.fairyTeller.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Component
@AllArgsConstructor
public class OAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException exception) throws IOException, ServletException {

            String errorMessage = "Unknown Error"; // 기본 예외 메시지

            // Exception 처리
            if (exception instanceof BadCredentialsException) {
                errorMessage = "Invalid Username or Password";
            } else if (exception instanceof LockedException) {
                errorMessage = "The account is locked";
            } else if (exception instanceof DisabledException) {
                errorMessage = "The account is disabled";
            } else if (exception instanceof AccountExpiredException) {
                errorMessage = "The account is expired";
            } else if (exception instanceof CredentialsExpiredException) {
                errorMessage = "Credentials have expired";
            } else if (exception instanceof SessionAuthenticationException) {
                errorMessage = "Session authentication failed";
            } else if (exception instanceof InsufficientAuthenticationException) {
                errorMessage = "Invalid Secret Key";
            } else if (exception instanceof InternalAuthenticationServiceException) {
                errorMessage = "Internal authentication service error";
            } else if (exception instanceof AuthenticationServiceException) {
                errorMessage = "Authentication service error";
            } else if (exception instanceof OAuth2AuthenticationException) {
                errorMessage = "OAuth2 authentication failed";
            }

            // 파라미터로 error와 exception을 보내서 controller에서 처리하기 위함.
            setDefaultFailureUrl("/login?error=true&exception=" + errorMessage);

            // 부모클래스의 onAuthenticationFailure로 처리를 위임하자.
            super.onAuthenticationFailure(request, response, exception);
        }
    }

