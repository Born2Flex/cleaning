package org.ukma.userserver.user;


import org.springframework.data.domain.Pageable;
import org.ukma.userserver.user.models.*;

import java.util.List;

public interface UserService {
    UserDto create(UserRegistrationDto user);
    UserDto update(UserDto user);
    UserDto getUser();
    UserDto getUserById(Long id);
    UserDto getByEmail(String email);
    UserDto updatePassword(UserPasswordDto user);
    Boolean deleteById(Long id);
    UserPageDto findUsersByPageAndRole(Role role, Pageable pageable);
    List<UserListDto> findUsersByRole(Role role);
}
