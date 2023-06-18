package jungle.fairyTeller.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static jungle.fairyTeller.security.RedirectUrlCookieFilter.REDIRECT_URI_PARAM;

@Slf4j
@Component
@AllArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final Environment environment;

    private static final String LOCAL_REDIRECT_URL = "http://localhost:3000";
    private static final String DEV_REDIRECT_URL = "http://www.fairy-teller.shop";

    private static String REDIRECT_URL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("auth succeeded");
        TokenProvider tokenProvider = new TokenProvider();
        String token = tokenProvider.create(authentication);

        Optional<Cookie> oCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(REDIRECT_URI_PARAM)).findFirst();

        Optional<String> redirectURi = oCookie.map(Cookie::getValue);

        String activeProfiles = environment.getProperty("spring.profiles.active");
        if (activeProfiles != null && activeProfiles.contains("dev")) {
            REDIRECT_URL = DEV_REDIRECT_URL;
        } else {
            REDIRECT_URL = LOCAL_REDIRECT_URL;
        }

        log.info("token {}", token);
        response.sendRedirect(redirectURi.orElseGet(() -> REDIRECT_URL) + "/sociallogin?token=" + token);
    }

}
