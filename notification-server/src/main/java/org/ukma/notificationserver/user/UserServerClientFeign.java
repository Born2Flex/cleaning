package org.ukma.notificationserver.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-server", path = "/api")
public interface UserServerClientFeign {
    @GetMapping("/auth/login")
    JwtResponse login(@RequestBody AuthRequest authRequest);
}
