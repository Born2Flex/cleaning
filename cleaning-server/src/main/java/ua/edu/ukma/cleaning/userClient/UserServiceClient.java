package ua.edu.ukma.cleaning.userClient;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class UserServiceClient {
    private final RestClient restClient;

//    public UserDto getUserById(Long id);
}
