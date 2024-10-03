package org.ukma.userserver.user;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.ukma.userserver.user.models.UserDto;
import org.ukma.userserver.user.models.UserListDto;
import org.ukma.userserver.user.models.UserRegistrationDto;

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
            @Mapping(target = "addressList", ignore = true)
    })
    void updateFields(@MappingTarget UserEntity entity, UserDto user);

    UserDto toDto(UserEntity user);

    List<UserListDto> toUserListDto(List<UserEntity> entities);

    @Named("encodePassword")
    default String encodePassword(String password, @Context PasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(password);
    }
}
