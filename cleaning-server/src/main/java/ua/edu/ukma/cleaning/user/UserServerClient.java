package ua.edu.ukma.cleaning.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import ua.edu.ukma.cleaning.security.JwtService;
import ua.edu.ukma.cleaning.user.dto.UserDto;
import ua.edu.ukma.cleaning.user.dto.UserListDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final RestClient restClient = RestClient.create(userServerUrl);

    private final JwtService jwtService;

    public UserDto getById(Long id) {
        return makeApiRequest("/api/users", HttpMethod.GET, null, UserDto.class).getBody();
    }

    public void updateUser(UserDto userDto) {
        makeApiRequest("/api/users", HttpMethod.PUT, userDto, Object.class);
    }

    public List<UserListDto> getAllByRole(Role role) {
        return ((List<UserListDto>) makeApiRequest("/api/users/by-role/" + role, HttpMethod.GET, null, List.class).getBody());
    }

    public <T> ResponseEntity<T> makeApiRequest(String endpoint, HttpMethod method, Object requestBody, Class<T> responseType) {
        if (authToken == null || jwtService.isTokenExpired(authToken)) {
            login();
        }
        try {
            return restClient.method(method)
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + authToken)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(responseType);
        } catch (Exception e) {
            log.info("Error making request", e);
            throw new RuntimeException(e);
        }
    }

    private void login() {
        AuthRequest authRequest = new AuthRequest(username, password);
        JwtResponse response = restClient.method(HttpMethod.POST)
                .uri("/api/auth/login")
                .body(authRequest)
                .retrieve()
                .toEntity(JwtResponse.class).getBody();
        authToken = response.getAccessToken();
    }
}
