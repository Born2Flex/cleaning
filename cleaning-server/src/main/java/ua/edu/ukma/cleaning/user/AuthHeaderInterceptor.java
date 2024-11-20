package ua.edu.ukma.cleaning.user;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.edu.ukma.cleaning.security.JwtService;

@Component
@RequiredArgsConstructor
public class AuthHeaderInterceptor implements RequestInterceptor {
    public static final String BEARER_PREFIX = "Bearer ";
    private final UserServerClientFeign userServerClientFeign;
    private final JwtService jwtService;
    @Value("${user.server.username}")
    private String username;
    @Value("${user.server.password}")
    private String password;

    private String token;

    @Override
    public void apply(RequestTemplate template) {
        if (token == null || jwtService.isTokenExpired(token))
            token = userServerClientFeign.login(new AuthRequest(username, password)).getAccessToken();
        template.header("Authorization", BEARER_PREFIX + token);
    }
}
