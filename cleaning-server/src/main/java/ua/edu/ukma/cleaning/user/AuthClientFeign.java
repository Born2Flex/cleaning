package ua.edu.ukma.cleaning.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-server", path = "/api", contextId = "auth-api")
public interface AuthClientFeign {

    @PostMapping("/auth/login")
    JwtResponse login(@RequestBody AuthRequest authRequest);

}
