package org.ukma.userserver.user;


import org.springframework.data.domain.Pageable;
import org.ukma.userserver.user.models.Role;
import org.ukma.userserver.user.models.UserDto;
import org.ukma.userserver.user.models.UserPageDto;
import org.ukma.userserver.user.models.UserPasswordDto;
import org.ukma.userserver.user.models.UserRegistrationDto;

public interface UserService {
    UserDto create(UserRegistrationDto user);
    UserDto update(UserDto user);
    UserDto getUser();
    UserDto getByEmail(String email);
    UserDto updatePassword(UserPasswordDto user);

    UserPageDto findUsersByPageAndRole(Role role, Pageable pageable);
}
