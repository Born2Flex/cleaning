package ua.edu.ukma.cleaning;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.edu.ukma.cleaning.employment.EmploymentDto;
import ua.edu.ukma.cleaning.employment.EmploymentRepository;
import ua.edu.ukma.cleaning.order.review.ReviewRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

@Slf4j
@Sql(value = {"/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/init2.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "storage.root-dir=src/test/resources/storage")
public class EmploymentControllerTest {
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
    private EmploymentRepository employmentRepository;

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
        employmentRepository.deleteAll();
    }

    @Test
    public void createEmploymentWithResumeTest() throws IOException {
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

        Assertions.assertFalse(Files.exists(Path.of(storageDirectory + "/employment/1.pdf")));

        RestAssured.baseURI = applicationUrl;
        RestAssured.port = applicationPort;
        EmploymentDto employmentDto = given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
                .multiPart("resume", new File("src/test/resources/testdata/practice-4.pdf"), "application/pdf")
                .when()
                .post("/api/employment")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().as(EmploymentDto.class);
        Assertions.assertTrue(Files.exists(Path.of(storageDirectory + "/employment/1.pdf")));

        Assertions.assertNotNull(employmentDto);
        Assertions.assertEquals(employmentDto.getId(), 1);

        byte[] originalFileBytes = Files.readAllBytes(Paths.get("src/test/resources/testdata/practice-4.pdf"));
        byte[] uploadedFileBytes = Files.readAllBytes(Paths.get(storageDirectory + "/employment/1.pdf"));
        Assertions.assertArrayEquals(originalFileBytes, uploadedFileBytes);
    }

    @Test
    public void getResumeFromServerTest() throws IOException {
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

        Assertions.assertFalse(Files.exists(Path.of(storageDirectory + "/employment/1.pdf")));

        RestAssured.baseURI = applicationUrl;
        RestAssured.port = applicationPort;
        given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
                .multiPart("resume", new File("src/test/resources/testdata/practice-4.pdf"), "application/pdf")
                .when()
                .post("/api/employment")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().as(EmploymentDto.class);
        Assertions.assertTrue(Files.exists(Path.of(storageDirectory + "/employment/2.pdf")));

        Response response = given()
                .header("Authorization", "Bearer " + loginResponse.getAccessToken())
                .when()
                .get(("/api/employment/load-resume"))
                .then()
                .statusCode(200)
                .contentType("application/pdf")
                .extract()
                .response();

        byte[] responseBytes = response.getBody().asByteArray();
        byte[] fileBytes = Files.readAllBytes(Paths.get("src/test/resources/testdata/practice-4.pdf"));
        Assertions.assertArrayEquals(responseBytes, fileBytes);
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
