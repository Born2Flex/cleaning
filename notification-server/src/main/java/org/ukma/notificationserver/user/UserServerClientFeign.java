package org.ukma.notificationserver.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-server", path = "/api")
public interface UserServerClientFeign {
    @PostMapping("/auth/login")
    JwtResponse login(@RequestBody AuthRequest authRequest);
}
