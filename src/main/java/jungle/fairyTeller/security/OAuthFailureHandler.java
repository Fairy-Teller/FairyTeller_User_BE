package jungle.fairyTeller.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
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
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "Invalid Username or Password"; // 기본 예외 메시지

        // exceprion 처리
        if(exception instanceof BadCredentialsException) {
            errorMessage = "Invalid Username or Password";
        }else if(exception instanceof InsufficientAuthenticationException) {
            errorMessage = "Invalid Secret Key";
        }
        // 파라미터로 error와 exception을 보내서 controller에서 처리하기 위함.
        setDefaultFailureUrl("/login?error=true&exception=" + errorMessage);

        // 부모클래스의 onAuthenticationFailure로 처리를 위임하자.
        super.onAuthenticationFailure(request, response, exception);
    }
}
