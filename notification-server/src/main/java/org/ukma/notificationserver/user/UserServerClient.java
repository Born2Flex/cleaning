package org.ukma.notificationserver.user;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.ukma.notificationserver.security.JwtService;
import org.ukma.notificationserver.user.dto.UserDto;
import org.ukma.notificationserver.user.dto.UserListDto;
import org.ukma.notificationserver.utils.exeptions.ClientRequestException;
import org.ukma.notificationserver.utils.exeptions.InvalidResponseException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServerClient {
    public static final String BEARER_PREFIX = "Bearer ";
    @Value("${user.server.url}")
    private String userServerUrl;

    @Value("${user.server.username}")
    private String username;

    @Value("${user.server.password}")
    private String password;

    private String authToken;

    private RestClient restClient;

    private final JwtService jwtService;

    @PostConstruct
    public void init() {
        restClient = RestClient.create(userServerUrl);
    }

    public <T> ResponseEntity<T> makeApiRequest(String endpoint, HttpMethod method, Object requestBody, Class<T> responseType) {
        if (authToken == null || jwtService.isTokenExpired(authToken)) {
            login();
        }
        try {
            RestClient.RequestBodySpec request = restClient.method(method)
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + authToken);
            if (requestBody != null)
                request.body(requestBody);
            return request.retrieve()
                    .toEntity(responseType);
        } catch (Exception e) {
            log.error("Error making request", e);
            throw new ClientRequestException("Error making request", e);
        }
    }

    private void login() {
        authToken = getJWTToken();
    }

    public String getJWTToken() {
        AuthRequest authRequest = new AuthRequest(username, password);
        JwtResponse response =  restClient.method(HttpMethod.POST)
                .uri("/api/auth/login")
                .body(authRequest)
                .retrieve()
                .toEntity(JwtResponse.class).getBody();
        if (response == null)
            throw new InvalidResponseException();
        return BEARER_PREFIX + response.getAccessToken();
    }
}
