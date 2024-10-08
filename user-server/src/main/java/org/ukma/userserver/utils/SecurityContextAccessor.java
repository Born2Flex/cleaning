package org.ukma.userserver.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.ukma.userserver.user.UserEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityContextAccessor {
    public static UserEntity getAuthenticatedUser() {
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long getAuthenticatedUserId() {
        return getAuthenticatedUser().getId();
    }

    public static List<String> getAuthorities() {
        return getAuthenticatedUser().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    }
}
