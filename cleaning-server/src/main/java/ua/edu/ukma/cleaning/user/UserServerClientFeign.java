package ua.edu.ukma.cleaning.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ua.edu.ukma.cleaning.user.dto.UserDto;
import ua.edu.ukma.cleaning.user.dto.UserListDto;

import java.util.List;

@FeignClient(name = "user-server", path = "/api")
public interface UserServerClientFeign {

    @GetMapping("/users/{id}")
    UserDto getById(@PathVariable Long id);

    @GetMapping("/users/by-role/{role}")
    List<UserListDto> getAllByRole(@PathVariable Role role);

    @PutMapping("/users")
    UserDto updateUser(@RequestBody UserDto userDto);

    @GetMapping("/auth/login")
    JwtResponse login(@RequestBody AuthRequest authRequest);
}
