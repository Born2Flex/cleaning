package ua.edu.ukma.cleaning.user;

import org.mapstruct.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.edu.ukma.cleaning.user.dto.EmployeeDto;
import ua.edu.ukma.cleaning.user.dto.UserDto;
import ua.edu.ukma.cleaning.user.dto.UserListDto;
import ua.edu.ukma.cleaning.user.dto.UserRegistrationDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mappings({
            @Mapping(target = "role", expression = "java(ua.edu.ukma.cleaning.user.Role.USER)"),
            @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    })
    UserEntity toEntity(UserRegistrationDto user, @Context PasswordEncoder passwordEncoder);

    UserEntity toEntity(UserDto user);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "addressList", ignore = true),
            @Mapping(target = "role", ignore = true)
    })
    void updateFields(@MappingTarget UserEntity entity, UserDto user);

    UserDto toDto(UserEntity user);

    EmployeeDto toEmployeeDto(UserEntity employee);

    List<EmployeeDto> toEmployeeDtoList(List<UserEntity> employees);

    List<UserListDto> toUserListDto(List<UserEntity> entities);

    @Named("encodePassword")
    default String encodePassword(String password, @Context PasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(password);
    }
}
