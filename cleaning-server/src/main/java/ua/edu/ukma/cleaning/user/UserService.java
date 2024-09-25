package ua.edu.ukma.cleaning.user;


import org.springframework.data.domain.Pageable;
import ua.edu.ukma.cleaning.user.dto.*;

public interface UserService {
    UserDto create(UserRegistrationDto user);
    UserDto update(UserDto user);
    UserDto getUser();
    UserDto getByEmail(String email);
    UserDto updatePassword(UserPasswordDto user);

    UserPageDto findUsersByPageAndRole(Role role, Pageable pageable);
}
