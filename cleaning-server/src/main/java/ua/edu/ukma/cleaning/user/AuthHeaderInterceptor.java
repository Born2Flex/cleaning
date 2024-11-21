package ua.edu.ukma.cleaning.user;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.edu.ukma.cleaning.security.JwtService;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthHeaderInterceptor implements RequestInterceptor {
    public static final String BEARER_PREFIX = "Bearer ";
    private final AuthClientFeign authClientFeign;
    private final JwtService jwtService;
    @Value("${user.server.username}")
    private String username;
    @Value("${user.server.password}")
    private String password;

    private String token;

    @Override
    public void apply(RequestTemplate template) {
        if (token == null || jwtService.isTokenExpired(token))
            token = authClientFeign.login(new AuthRequest(username, password)).getAccessToken();
        template.header("Authorization", BEARER_PREFIX + token);
    }
}
