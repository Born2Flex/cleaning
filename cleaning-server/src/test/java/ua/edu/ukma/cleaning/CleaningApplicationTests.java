package ua.edu.ukma.cleaning;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class CleaningApplicationTests {

    @Test
    void contextLoads() {
        var modules = ApplicationModules.of(CleaningApplication.class);
        modules.verify();
        modules.forEach(System.out::println);
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }

}
