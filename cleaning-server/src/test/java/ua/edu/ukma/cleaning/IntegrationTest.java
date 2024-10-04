package ua.edu.ukma.cleaning;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.edu.ukma.cleaning.employment.EmploymentRepository;
import ua.edu.ukma.cleaning.order.OrderRepository;
import ua.edu.ukma.cleaning.order.review.ReviewRepository;
import ua.edu.ukma.cleaning.security.JwtService;
import ua.edu.ukma.cleaning.user.AuthenticatedUser;
import ua.edu.ukma.cleaning.user.Role;

import java.io.File;

@Slf4j
@Sql(value = {"/init2.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "storage.root-dir=src/test/resources/storage")
public class IntegrationTest {
    @LocalServerPort
    protected int serverPort;
    @Autowired
    private EmploymentRepository employmentRepository;
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected ReviewRepository reviewRepository;
    @Value("${storage.root-dir:src/test/resources/storage}")
    protected String storageDirectory;
    protected AuthenticatedUser user = new AuthenticatedUser(1L, Role.USER, "user@gmail.com");
    protected AuthenticatedUser employee = new AuthenticatedUser(2L, Role.EMPLOYEE, "employee@gmail.com");
    protected AuthenticatedUser admin = new AuthenticatedUser(3L, Role.ADMIN, "admin@gmail.com");
    protected String userToken = "userTestToken";
    protected String employeeToken = "employeeTestToken";
    protected String adminToken = "adminTestToken";

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;
    }

    @AfterEach
    public void afterEachTest() {
        File storage = new File(storageDirectory);
        deleteDirectory(storage);
        orderRepository.deleteAll();
        employmentRepository.deleteAll();
        reviewRepository.deleteAll();
    }

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test");

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void initialize(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
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
