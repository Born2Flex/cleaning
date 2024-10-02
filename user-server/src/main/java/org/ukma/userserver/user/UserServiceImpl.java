package org.ukma.userserver.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ukma.userserver.exceptions.EmailDuplicateException;
import org.ukma.userserver.exceptions.NoSuchEntityException;
import org.ukma.userserver.exceptions.PhoneNumberDuplicateException;
import org.ukma.userserver.user.models.Role;
import org.ukma.userserver.user.models.UserDto;
import org.ukma.userserver.user.models.UserPageDto;
import org.ukma.userserver.user.models.UserPasswordDto;
import org.ukma.userserver.user.models.UserRegistrationDto;
import org.ukma.userserver.utils.SecurityContextAccessor;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserRegistrationDto user) {
        if (userRepository.findUserEntityByEmail(user.getEmail()).isPresent()) {
            throw new EmailDuplicateException("Email already in use!");
        }
        UserDto userDto = userMapper.toDto(userRepository.save(userMapper.toEntity(user, encoder)));
        log.info("Created new user with id = {}", userDto.getId());
        return userDto;
    }

    @Override
    public UserDto update(UserDto user) {
        UserEntity userEntity = SecurityContextAccessor.getAuthenticatedUser();
        Optional<UserEntity> userEntityByEmail = userRepository.findUserEntityByEmail(user.getEmail());
        userEntityByEmail.ifPresent(existingUser -> {
            if (!Objects.equals(userEntity.getId(), existingUser.getId())) {
                log.info("User id = {} try to use email that is already in use", userEntity.getId());
                throw new EmailDuplicateException("Email already in use!");
            }
        });
        Optional<UserEntity> userEntityByPhone = userRepository.findUserEntityByPhoneNumber(user.getPhoneNumber());
        userEntityByPhone.ifPresent(existingUser -> {
            if (!Objects.equals(userEntity.getId(), existingUser.getId())) {
                log.info("User id = {} try to use phone number that is already in use", userEntity.getId());
                throw new PhoneNumberDuplicateException("Phone number already in use!");
            }
        });
        userMapper.updateFields(userEntity, user);
        log.debug("Data of user id = {} successfully updated", userEntity.getId());
        return userMapper.toDto(userRepository.save(userEntity));
    }

    @Override
    public UserDto getUser() {
        return userMapper.toDto(SecurityContextAccessor.getAuthenticatedUser());
    }

    @Override
    public UserDto getByEmail(String email) {
        UserEntity userEntity = userRepository.findUserEntityByEmail(email).orElseThrow(
                () -> new NoSuchEntityException("Can`t find user by email: " + email)
        );
        return userMapper.toDto(userEntity);
    }

    @Transactional
    @Override
    public UserDto updatePassword(UserPasswordDto user) {
        UserEntity userEntity = SecurityContextAccessor.getAuthenticatedUser();
        userEntity.setPassword(encoder.encode(user.getPassword()));
        log.info("Password of user id = {} was changed", user.getId());
        return userMapper.toDto(userRepository.save(userEntity));
    }

    @Override
    public UserPageDto findUsersByPageAndRole(Role role, Pageable pageable) {
        Page<UserEntity> users = userRepository.findAllByRole(role, pageable);
        int totalPages = users.getTotalPages();
        return new UserPageDto(pageable.getPageNumber(), totalPages, userMapper.toUserListDto(users.stream().toList()));
    }
}
