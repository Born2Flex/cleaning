package ua.edu.ukma.cleaning.user;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHeaderInterceptor implements RequestInterceptor {
    private final UserServerClient userServerClient;

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", userServerClient.getJWTToken());
    }
}
