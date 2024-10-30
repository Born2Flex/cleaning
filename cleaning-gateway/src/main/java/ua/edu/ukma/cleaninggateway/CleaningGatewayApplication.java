package ua.edu.ukma.cleaninggateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CleaningGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CleaningGatewayApplication.class, args);
    }

}
