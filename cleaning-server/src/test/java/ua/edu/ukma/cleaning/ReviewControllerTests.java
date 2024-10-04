package ua.edu.ukma.cleaning;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.edu.ukma.cleaning.order.dto.OrderForUserDto;
import ua.edu.ukma.cleaning.order.review.ReviewDto;
import ua.edu.ukma.cleaning.order.review.ReviewEntity;
import ua.edu.ukma.cleaning.order.review.ReviewRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import io.restassured.module.jsv.JsonSchemaValidator;

@Slf4j
@Sql(value = {"/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/init2.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "storage.root-dir=src/test/resources/storage")
public class ReviewControllerTests {

    @LocalServerPort
    private int applicationPort;
    private String applicationUrl = "http://localhost";

    private static String userCleaningUrl;
    private static Integer userCleaningPort;

    @Value("${storage.root-dir:src/test/resources/storage}")
    private String storageDirectory;

    private String userData = "{\"username\": \"m.burnatt@gmail.com\",\"password\": \"Qw3rty*\"}";
    private String employeeData = "{\"username\": \"c.burnett@outlook.com\",\"password\": \"P4ssw()rd\"}";
    private String adminData = "{\"username\": \"admin\",\"password\": \"admin\"}";

    @Autowired
    private ReviewRepository reviewRepository;

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("unit-test")
            .withUsername("test")
            .withPassword("test");

    @Container
    public static DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("src/test/resources/user-cleaning.yaml"))
                    .withExposedService("user-cleaning_1", 1177, Wait.forHttp("/api/auth/login").withMethod("POST").forStatusCode(500));

    @DynamicPropertySource
    static void initialize(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    public static void setup() {
        userCleaningUrl = "http://" + environment.getServiceHost("user-cleaning_1", 1177);
        userCleaningPort = environment.getServicePort("user-cleaning_1", 1177);
    }

    @AfterEach
    public void afterEachTest() {
        File storage = new File(storageDirectory);
        deleteDirectory(storage);
    }

    @Test
    public void loginTest() throws InterruptedException {
        RestAssured.baseURI = userCleaningUrl;
        RestAssured.port = userCleaningPort;
        given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200);
    }

    @Test
    public void getReviewByIdUnauthorizedTest() {
        RestAssured.baseURI = applicationUrl;
        RestAssured.port = applicationPort;
        given()
                .when()
                .get("/api/orders/review/1")
                .then()
                .statusCode(500);

    }

    @Test
    public void getReviewByIdEmployeeTest() {
        RestAssured.baseURI = userCleaningUrl;
        RestAssured.port = userCleaningPort;
        LoginResponse loginResponse = given()
                .contentType("application/json")
                .body(employeeData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().as(LoginResponse.class);

        RestAssured.baseURI = applicationUrl;
        RestAssured.port = applicationPort;
        given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
                .when()
                .get("/api/orders/review/1")
                .then()
                .statusCode(500);
    }

    @Test
    public void getReviewByIdUserTest() {
        RestAssured.baseURI = userCleaningUrl;
        RestAssured.port = userCleaningPort;
        LoginResponse loginResponse = given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().as(LoginResponse.class);

        RestAssured.baseURI = applicationUrl;
        RestAssured.port = applicationPort;
        ReviewDto reviewDto = given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
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
        RestAssured.baseURI = userCleaningUrl;
        RestAssured.port = userCleaningPort;
        LoginResponse loginResponse = given()
                .contentType("application/json")
                .body(adminData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().as(LoginResponse.class);

        RestAssured.baseURI = applicationUrl;
        RestAssured.port = applicationPort;
        ReviewDto reviewDto = given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
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
        deleteDirectory(new File(storageDirectory));
        RestAssured.baseURI = userCleaningUrl;
        RestAssured.port = userCleaningPort;
        LoginResponse loginResponse = given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().as(LoginResponse.class);

        Assertions.assertFalse(Files.exists(Path.of(storageDirectory + "/review/2.jpg")));

        RestAssured.baseURI = applicationUrl;
        RestAssured.port = applicationPort;
        OrderForUserDto orderDto = given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
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
        deleteDirectory(new File(storageDirectory));
        RestAssured.baseURI = userCleaningUrl;
        RestAssured.port = userCleaningPort;
        LoginResponse loginResponse = given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().as(LoginResponse.class);

        Assertions.assertFalse(Files.exists(Path.of(storageDirectory + "/review/2.pdf")));

        RestAssured.baseURI = applicationUrl;
        RestAssured.port = applicationPort;
        given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
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
        RestAssured.baseURI = userCleaningUrl;
        RestAssured.port = userCleaningPort;
        LoginResponse loginResponse = given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().as(LoginResponse.class);

        Assertions.assertFalse(Files.exists(Path.of(storageDirectory + "/review/2.jpg")));

        RestAssured.baseURI = applicationUrl;
        RestAssured.port = applicationPort;
        given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
                .multiPart("review", "{\"orderId\": 2,\"cleaningRate\": 1,\"employeeRate\": 1,\"details\": \"hello\"}", "application/json")
                .multiPart("image", new File("src/test/resources/testdata/spring-boot.jpg"), "image/jpeg")
                .when()
                .post("/api/orders/review/2/with-image")
                .then()
                .statusCode(200)
                .contentType("application/json");

        Assertions.assertTrue(Files.exists(Path.of(storageDirectory + "/review/2.jpg")));

        Response response = given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
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
        RestAssured.baseURI = userCleaningUrl;
        RestAssured.port = userCleaningPort;
        LoginResponse loginResponse = given()
                .contentType("application/json")
                .body(userData)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().as(LoginResponse.class);

        Assertions.assertFalse(Files.exists(Path.of(storageDirectory + "/review/2.jpg")));

        RestAssured.baseURI = applicationUrl;
        RestAssured.port = applicationPort;
        given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
                .multiPart("review", "{\"orderId\": 2,\"cleaningRate\": 1,\"employeeRate\": 1,\"details\": \"hello\"}", "application/json")
                .multiPart("image", new File("src/test/resources/testdata/spring-boot.jpg"), "image/jpeg")
                .when()
                .post("/api/orders/review/2/with-image")
                .then()
                .statusCode(200)
                .contentType("application/json");

        Assertions.assertTrue(Files.exists(Path.of(storageDirectory + "/review/2.jpg")));

        given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
                .when()
                .get(("/api/orders/review/2/with-image"))
                .then()
                .statusCode(200)
                .contentType("multipart/form-data");
    }

    private boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        return directory.delete();
    }

}
