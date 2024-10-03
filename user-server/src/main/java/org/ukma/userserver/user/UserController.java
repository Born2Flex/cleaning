package org.ukma.userserver.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.ukma.userserver.user.models.*;

import java.util.List;

@Slf4j
@RestController()
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Endpoint for operations with users (customers/staff)")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get user by id", description = "Get user by id")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public UserDto getUser() {
        return userService.getUser();
    }

    @Operation(summary = "Get user by email", description = "Get user by email")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/by-email/{email}")
    public UserDto getUserByEmail(@PathVariable String email) {
        return userService.getByEmail(email);
    }

    @Operation(summary = "Change user", description = "Change user")
    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public UserDto updateUser(@RequestBody @Valid UserDto userDto) {
        return userService.update(userDto);
    }

    @Operation(summary = "Change user", description = "Change user")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/pass")
    public UserDto updatePassword(@RequestBody @Valid UserPasswordDto userDto) {
        return userService.updatePassword(userDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add user", description = "Add user")
    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserRegistrationDto userDto) {
        return userService.create(userDto);
    }

    @Operation(summary = "Find all users by Role", description = "Find all users by Role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/findAllByRole")
    public UserPageDto findAllByRole(@RequestParam(defaultValue = "USER") Role role, @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return userService.findUsersByPageAndRole(role, pageable);
    }

    @Operation(summary = "Find all users by Role", description = "Find all users by Role")
    @PreAuthorize("hasAuthority('ROLE_CLEANING_SERVER')")
    @GetMapping("/by-role/{role}")
    public List<UserListDto> findAllByRole(@PathVariable Role role) {
        return userService.findUsersByRole(role);
    }
}
