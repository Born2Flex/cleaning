package ua.edu.ukma.cleaning;

import groovy.util.logging.Slf4j;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ua.edu.ukma.cleaning.address.AddressDto;
import ua.edu.ukma.cleaning.order.dto.OrderCreationDto;
import ua.edu.ukma.cleaning.user.Role;
import ua.edu.ukma.cleaning.user.server.UserServerJwtService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Slf4j
class SecurityTest extends IntegrationTest {
    private final UserServerJwtService userServerJwtService = new UserServerJwtService();

    @Test
    void userCanCreateOrderTest() {
        OrderCreationDto orderDto = new OrderCreationDto(1000.0, LocalDateTime.now(), null, "fast",
                new AddressDto("Kyiv", "Balzaka", "20", "214", "02224"), Duration.ofHours(2),
                Map.of(1L,1));
        given()
                .header("Authorization", "Bearer " + generateTokenAsUserServer(1L, Role.USER, "testEmail"))
                .body(orderDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType("application/json");
    }

    @Test
    void adminCanCreateOrderTest() {
        OrderCreationDto orderDto = new OrderCreationDto(1000.0, LocalDateTime.now(), null, "fast",
                new AddressDto("Kyiv", "Balzaka", "20", "214", "02224"), Duration.ofHours(2),
                Map.of(1L,1));
        given()
                .header("Authorization", "Bearer " + generateTokenAsUserServer(1L, Role.ADMIN, "testEmail"))
                .body(orderDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .contentType("application/json");
    }

    public String generateTokenAsUserServer(Long id, Role role, String email) {
        return userServerJwtService.generateToken(email, role, id);
    }
}
