package ua.edu.ukma.cleaning;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class CleaningApplicationTests {

    @Test
    void contextLoads() {
        var modules = ApplicationModules.of(CleaningApplication.class);
        modules.verify();
        modules.forEach(System.out::println);
    }

}
