package ua.edu.ukma.cleaning.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ua.edu.ukma.cleaning.user.AuthenticatedUser;
import ua.edu.ukma.cleaning.user.Role;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {
    @Value("${jwt.secret}")
    public String secret;

    private class JWTClaims {
        public static String ROLE_CLAIM = "role";
        public static String ID_CLAIM = "id";
    }

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public AuthenticatedUser extractUser(String token) {
        Long id = extractClaim(token, claims -> claims.get(JWTClaims.ID_CLAIM, Long.class));
        Role role = Role.valueOf(extractClaim(token, claims -> claims.get(JWTClaims.ROLE_CLAIM, String.class)));
        String username = extractUsername(token);
        return new AuthenticatedUser(id, role, username);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
