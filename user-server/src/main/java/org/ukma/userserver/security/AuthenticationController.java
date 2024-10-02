package org.ukma.userserver.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ukma.userserver.exceptions.CantRefreshTokenException;
import org.ukma.userserver.security.dto.AuthRequest;
import org.ukma.userserver.security.dto.JwtResponse;
import org.ukma.userserver.security.refresh.tokens.RefreshTokenEntity;
import org.ukma.userserver.security.refresh.tokens.RefreshTokenService;
import org.ukma.userserver.user.UserEntity;
import org.ukma.userserver.user.models.UserRegistrationDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthenticationController {
    private final CustomUserDetails service;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @PreAuthorize("isAnonymous()")
    @PostMapping("/registration")
    public Boolean addNewUser(@RequestBody UserRegistrationDto dto) {
        return service.register(dto);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>("{\"errors\":\"Invalid username or password!\"}", headers, HttpStatus.UNAUTHORIZED);
        }
        UserEntity user = (UserEntity) service.loadUserByUsername(authRequest.getUsername());
        String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return ResponseEntity.ok()
                .headers(headers)
                .body(new JwtResponse(token, refreshTokenService.create(authRequest.getUsername())));
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/refreshToken")
    public JwtResponse refreshToken(@RequestBody String refreshToken) {
        RefreshTokenEntity token = refreshTokenService.findByToken(refreshToken).orElseThrow(
                () -> new CantRefreshTokenException("Can`t find refresh token")
        );
        refreshTokenService.verify(token);
        return new JwtResponse(jwtService.generateToken(token.getUser().getEmail(), token.getUser().getRole(), token.getUser().getId()), refreshToken);
    }
}
