package ua.edu.ukma.cleaning.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserServerClient {
    @Value("${user.server.url}")
    private String userServerUrl;

    @Value("${user.server.username}")
    private String username;

    @Value("${user.server.password}")
    private String password;

    private String authToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> makeApiRequest(String endpoint, HttpMethod method, Map<String, Object> requestBody) {
        if (authToken == null || isTokenExpired()) {
            login();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            return restTemplate.exchange(userServerUrl + endpoint, method, entity, String.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            login();
            headers.setBearerAuth(authToken);
            entity = new HttpEntity<>(requestBody, headers);
            return restTemplate.exchange(userServerUrl + endpoint, method, entity, String.class);
        }
    }

    private void login() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<JwtResponse> response = restTemplate.exchange(userServerUrl + "/api/login", HttpMethod.POST, entity, JwtResponse.class);
        authToken = response.getBody().getAccessToken();
    }

    private boolean isTokenExpired() {
        return false;
    }
}
