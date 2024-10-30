package ua.edu.ukma.cleaning.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ua.edu.ukma.cleaning.user.dto.UserDto;
import ua.edu.ukma.cleaning.user.dto.UserListDto;

import java.util.List;

@FeignClient(name = "user-server", path = "/api/users")
public interface UserServerClientFeign {

    @GetMapping("/{id}")
    UserDto getById(@PathVariable Long id);

    @GetMapping("/by-role/{role}")
    List<UserListDto> getAllByRole(@PathVariable Role role);
}
