package ua.edu.ukma.cleaning;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import ua.edu.ukma.cleaning.address.AddressDto;
import ua.edu.ukma.cleaning.order.OrderEntity;
import ua.edu.ukma.cleaning.order.Status;
import ua.edu.ukma.cleaning.order.dto.OrderForUserDto;
import ua.edu.ukma.cleaning.order.review.ReviewDto;
import ua.edu.ukma.cleaning.order.review.ReviewEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import io.restassured.module.jsv.JsonSchemaValidator;

@Slf4j
public class ReviewControllerTests extends IntegrationTest {
//    @Test
//    public void loginTest() throws InterruptedException {
//        RestAssured.baseURI = userCleaningUrl;
//        RestAssured.port = userCleaningPort;
//        given()
//                .contentType("application/json")
//                .body(userData)
//                .when()
//                .post("/api/auth/login")
//                .then()
//                .statusCode(200);
//    }


    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        ReviewEntity review = reviewRepository.save(new ReviewEntity(null, 5L, 5L, "good"));
        orderRepository.save(new OrderEntity(0l, 1100.0,
                LocalDateTime.of(2023,8,22,18,0),
                LocalDateTime.of(2023,9,1,12,0),
                user.getUsername(),null, null,
                new AddressDto("Kyiv", "Balzaka", "20", "214", "02224"), review,
                Status.DONE, Duration.ofHours(2), null));
        orderRepository.save(new OrderEntity(0l, 1100.0,
                LocalDateTime.of(2023,8,22,18,0),
                LocalDateTime.of(2023,9,1,12,0),
                user.getUsername(),null, null,
                new AddressDto("Kyiv", "Balzaka", "20", "214", "02224"), null,
                Status.DONE, Duration.ofHours(2), null));
    }

    @Test
    public void getReviewByIdUnauthorizedTest() {
        given()
                .when()
                .get("/api/orders/review/1")
                .then()
                .statusCode(500);

    }

    @Test
    public void getReviewByIdEmployeeTest() {
        given()
                .header("Authorization", "Bearer " + employeeToken)
                .when()
                .get("/api/orders/review/1")
                .then()
                .statusCode(500);
    }

    @Test
    public void getReviewByIdUserTest() {
        ReviewDto reviewDto = given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/api/orders/review/1")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/reviewdto-schema.json")))
                .extract().as(ReviewDto.class);
        ReviewEntity reviewEntity = reviewRepository.findById(1L).orElse(null);
        Assertions.assertNotNull(reviewEntity);
        Assertions.assertEquals(reviewDto.getOrderId(), reviewEntity.getId());
        Assertions.assertEquals(reviewDto.getCleaningRate(), reviewEntity.getCleaningRate());
        Assertions.assertEquals(reviewDto.getEmployeeRate(), reviewEntity.getEmployeeRate());
        Assertions.assertEquals(reviewDto.getDetails(), reviewEntity.getDetails());
    }

    @Test
    public void getReviewByIdAdminTest() {
        ReviewDto reviewDto = given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/orders/review/1")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/reviewdto-schema.json")))
                .extract().as(ReviewDto.class);
        ReviewEntity reviewEntity = reviewRepository.findById(1L).orElse(null);
        Assertions.assertNotNull(reviewEntity);
        Assertions.assertEquals(reviewDto.getOrderId(), reviewEntity.getId());
        Assertions.assertEquals(reviewDto.getCleaningRate(), reviewEntity.getCleaningRate());
        Assertions.assertEquals(reviewDto.getEmployeeRate(), reviewEntity.getEmployeeRate());
        Assertions.assertEquals(reviewDto.getDetails(), reviewEntity.getDetails());
    }

    @Test
    public void createReviewWithImageTest() throws IOException {
        OrderForUserDto orderDto = given()
                .header("Authorization", "Bearer " + userToken)
                .multiPart("review", "{\"orderId\": 2,\"cleaningRate\": 1,\"employeeRate\": 1,\"details\": \"hello\"}", "application/json")
                .multiPart("image", new File("src/test/resources/testdata/spring-boot.jpg"), "image/jpeg")
                .when()
                .post("/api/orders/review/2/with-image")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().as(OrderForUserDto.class);
        Assertions.assertTrue(Files.exists(Path.of(storageDirectory + "/review/2.jpg")));

        ReviewDto reviewDto = orderDto.getReview();
        Assertions.assertNotNull(reviewDto);
        Assertions.assertEquals(reviewDto.getCleaningRate(), 1L);
        Assertions.assertEquals(reviewDto.getEmployeeRate(), 1L);
        Assertions.assertEquals(reviewDto.getDetails(), "hello");

        byte[] originalFileBytes = Files.readAllBytes(Paths.get("src/test/resources/testdata/spring-boot.jpg"));
        byte[] uploadedFileBytes = Files.readAllBytes(Paths.get(storageDirectory + "/review/2.jpg"));
        Assertions.assertArrayEquals(originalFileBytes, uploadedFileBytes);
    }

    @Test
    public void createReviewWithImageUnsupportedTypeTest() throws IOException {
        given()
                .header("Authorization", "Bearer " + userToken)
                .multiPart("review", "{\"orderId\": 2,\"cleaningRate\": 1,\"employeeRate\": 1,\"details\": \"hello\"}", "application/json")
                .multiPart("image", new File("src/test/resources/testdata/practice-4.pdf"), "application/pdf")
                .when()
                .post("/api/orders/review/2/with-image")
                .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .contentType("application/json");
        Assertions.assertFalse(Files.exists(Path.of(storageDirectory + "/review/2.pdf")));
    }

    @Test
    public void getReviewImageTest() throws IOException {
        given()
                .header("Authorization", "Bearer " + userToken)
                .multiPart("review", "{\"orderId\": 2,\"cleaningRate\": 1,\"employeeRate\": 1,\"details\": \"hello\"}", "application/json")
                .multiPart("image", new File("src/test/resources/testdata/spring-boot.jpg"), "image/jpeg")
                .when()
                .post("/api/orders/review/2/with-image")
                .then()
                .statusCode(200)
                .contentType("application/json");

        Assertions.assertTrue(Files.exists(Path.of(storageDirectory + "/review/2.jpg")));

        Response response = given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(("/api/orders/review/2/image"))
                .then()
                .statusCode(200)
                .contentType("image/jpeg")
                .extract()
                .response();

        byte[] responseBytes = response.getBody().asByteArray();
        byte[] fileBytes = Files.readAllBytes(Paths.get("src/test/resources/testdata/spring-boot.jpg"));
        Assertions.assertArrayEquals(responseBytes, fileBytes);
    }

    @Test
    public void getReviewWithImageTest() {
        Assertions.assertFalse(Files.exists(Path.of(storageDirectory + "/review/2.jpg")));
        given()
                .header("Authorization", "Bearer " + userToken)
                .multiPart("review", "{\"orderId\": 2,\"cleaningRate\": 1,\"employeeRate\": 1,\"details\": \"hello\"}", "application/json")
                .multiPart("image", new File("src/test/resources/testdata/spring-boot.jpg"), "image/jpeg")
                .when()
                .post("/api/orders/review/2/with-image")
                .then()
                .statusCode(200)
                .contentType("application/json");

        Assertions.assertTrue(Files.exists(Path.of(storageDirectory + "/review/2.jpg")));

        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(("/api/orders/review/2/with-image"))
                .then()
                .statusCode(200)
                .contentType("multipart/form-data");
    }
}
