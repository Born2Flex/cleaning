package ua.edu.ukma.cleaning;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ua.edu.ukma.cleaning.employment.EmploymentDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class EmploymentControllerTest extends IntegrationTest {

    @Test
    public void createEmploymentWithResumeTest() throws IOException {
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
        Assertions.assertEquals(employmentDto.getId(), 1);

        byte[] originalFileBytes = Files.readAllBytes(Paths.get("src/test/resources/testdata/practice-4.pdf"));
        byte[] uploadedFileBytes = Files.readAllBytes(Paths.get(storageDirectory + "/employment/1.pdf"));
        Assertions.assertArrayEquals(originalFileBytes, uploadedFileBytes);
    }

    @Test
    public void getResumeFromServerTest() throws IOException {
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
