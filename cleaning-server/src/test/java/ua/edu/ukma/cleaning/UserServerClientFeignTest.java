package ua.edu.ukma.cleaning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import ua.edu.ukma.cleaning.user.*;
import ua.edu.ukma.cleaning.user.dto.UserDto;
import ua.edu.ukma.cleaning.user.dto.UserListDto;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("feign-client-test")
class UserServerClientFeignTest {
    @Autowired
    private AuthClientFeign authClientFeign;
    @Autowired
    private UserServerClientFeign userServerClientFeign;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RegisterExtension
    static WireMockExtension userServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8081))
            .build();

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public ServiceInstanceListSupplier serviceInstanceListSupplier() {
            return new TestServiceInstanceListSupplier("user-server", 8081);
        }
    }

    @Test
    void getById() {
        userServer.stubFor(get(urlEqualTo("/api/users/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "id": 1,
                          "name": "John Doe",
                          "email": "john.doe@example.com"
                        }
                        """)));

        UserDto user = userServerClientFeign.getById(1L);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());

        userServer.verify(getRequestedFor(urlEqualTo("/api/users/1")));
    }

    @Test
    void getAllByRole() {
        userServer.stubFor(get(urlEqualTo("/api/users/by-role/EMPLOYEE"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        [
                          {"id": 1, "name": "John Doe"},
                          {"id": 2, "name": "Jane Smith"}
                        ]
                        """)));

        List<UserListDto> users = userServerClientFeign.getAllByRole(Role.EMPLOYEE);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("Jane Smith", users.get(1).getName());

        userServer.verify(getRequestedFor(urlEqualTo("/api/users/by-role/EMPLOYEE")));
    }

    @Test
    void updateUser() throws JsonProcessingException {
        userServer.stubFor(put(urlEqualTo("/api/users"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                          {"id": 1, "name": "John Doe"}
                        """)));

        UserDto expected = new UserDto(1L, "John", "Doe", "Olegovich", "john.gmail.com", Role.EMPLOYEE, "+380978731876", Collections.emptyList());
        userServerClientFeign.updateUser(expected);

        userServer.verify(putRequestedFor(urlEqualTo("/api/users")).withRequestBody(equalToJson(objectMapper.writeValueAsString(expected))));
    }

    @Test
    void login() throws JsonProcessingException {
        userServer.stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "accessToken": "jwt-token",
                          "refreshToken": "refresh-token"
                        }
                        """)));

        AuthRequest expected = new AuthRequest("user", "password");
        JwtResponse jwtResponse = authClientFeign.login(expected);

        assertNotNull(jwtResponse);
        assertEquals("jwt-token", jwtResponse.getAccessToken());
        assertEquals("refresh-token", jwtResponse.getRefreshToken());

        userServer.verify(postRequestedFor(urlEqualTo("/api/auth/login")).withRequestBody(equalToJson(objectMapper.writeValueAsString(expected))));
    }
}
