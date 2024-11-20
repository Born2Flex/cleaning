package ua.edu.ukma.cleaning;

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
import ua.edu.ukma.cleaning.user.AuthRequest;
import ua.edu.ukma.cleaning.user.JwtResponse;
import ua.edu.ukma.cleaning.user.Role;
import ua.edu.ukma.cleaning.user.UserServerClientFeign;
import ua.edu.ukma.cleaning.user.dto.UserDto;
import ua.edu.ukma.cleaning.user.dto.UserListDto;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("feign-client-test")
class UserServerClientFeignTest {
    @Autowired
    private UserServerClientFeign userServerClientFeign;

    @RegisterExtension
    static WireMockExtension userServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8081))
            .build();

    @TestConfiguration
    public static class TestConfig { // Mock Eureka
        @Bean
        public ServiceInstanceListSupplier serviceInstanceListSupplier() {
            return new TestServiceInstanceListSupplier("user-server", 8081);
        }
    }

    @Test
    void testGetById() {
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
    void testGetAllByRole() {
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
    void testLogin() {
        userServer.stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "accessToken": "jwt-token",
                          "refreshToken": "refresh-token"
                        }
                        """)));

        AuthRequest authRequest = new AuthRequest("user", "password");
        JwtResponse jwtResponse = userServerClientFeign.login(authRequest);

        assertNotNull(jwtResponse);
        assertEquals("jwt-token", jwtResponse.getAccessToken());
        assertEquals("refresh-token", jwtResponse.getRefreshToken());

        userServer.verify(postRequestedFor(urlEqualTo("/api/auth/login")));
    }
}