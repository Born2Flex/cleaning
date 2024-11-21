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
import ua.edu.ukma.cleaning.user.AuthClientFeign;
import ua.edu.ukma.cleaning.user.AuthRequest;
import ua.edu.ukma.cleaning.user.JwtResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("feign-client-test")
class AuthFeignClientTest {
    @Autowired
    private AuthClientFeign authClientFeign;
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
