package ua.edu.ukma.cleaning.utils.security;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.edu.ukma.cleaning.user.UserRepository;
import ua.edu.ukma.cleaning.user.UserService;
import ua.edu.ukma.cleaning.user.dto.UserRegistrationDto;

@Service
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserEntityByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can`t find user by email: " + username));
    }

    public Boolean register(UserRegistrationDto dto) {
        userService.create(dto);
        return true;
    }
}
