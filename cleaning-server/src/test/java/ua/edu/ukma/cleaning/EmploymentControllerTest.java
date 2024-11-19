package ua.edu.ukma.cleaning;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.edu.ukma.cleaning.employment.EmploymentDto;
import ua.edu.ukma.cleaning.security.JwtService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

class EmploymentControllerTest extends IntegrationTest {
    @MockBean
    private JwtService jwtService;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        Mockito.when(jwtService.validateToken(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(jwtService.extractUser(userToken)).thenReturn(user);
        Mockito.when(jwtService.extractUser(employeeToken)).thenReturn(employee);
        Mockito.when(jwtService.extractUser(adminToken)).thenReturn(admin);
    }

    @Test
    void createEmploymentWithResumeTest() throws IOException {
        Assertions.assertFalse(Files.exists(Path.of(storageDirectory + "/employment/1.pdf")));
        EmploymentDto employmentDto = given()
                .header("Authorization", "Bearer " + userToken)
                .multiPart("resume", new File("src/test/resources/testdata/practice-4.pdf"), "application/pdf")
                .when()
                .post("/api/employment")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().as(EmploymentDto.class);
        Assertions.assertTrue(Files.exists(Path.of(storageDirectory + "/employment/1.pdf")));

        Assertions.assertNotNull(employmentDto);
        Assertions.assertEquals(1, employmentDto.getId());

        byte[] originalFileBytes = Files.readAllBytes(Paths.get("src/test/resources/testdata/practice-4.pdf"));
        byte[] uploadedFileBytes = Files.readAllBytes(Paths.get(storageDirectory + "/employment/1.pdf"));
        Assertions.assertArrayEquals(originalFileBytes, uploadedFileBytes);
    }

    @Test
    void getResumeFromServerTest() throws IOException {
        Assertions.assertFalse(Files.exists(Path.of(storageDirectory + "/employment/1.pdf")));
        given()
                .header("Authorization", "Bearer " + userToken)
                .multiPart("resume", new File("src/test/resources/testdata/practice-4.pdf"), "application/pdf")
                .when()
                .post("/api/employment")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract().as(EmploymentDto.class);
        Assertions.assertTrue(Files.exists(Path.of(storageDirectory + "/employment/2.pdf")));

        Response response = given()
                .header("Authorization", "Bearer " + userToken)
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
}
