package ua.edu.ukma.cleaning.utils.exceptionHandler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class UserServiceClientConfig {
    @Value(value = "${application.config.user-service-url}")
    private String userServiceUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.create(userServiceUrl);
    }
}
