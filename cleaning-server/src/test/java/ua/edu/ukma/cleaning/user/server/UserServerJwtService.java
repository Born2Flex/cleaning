package ua.edu.ukma.cleaning.user.server;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ua.edu.ukma.cleaning.user.Role;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserServerJwtService {
    public String secret = "53c7566B597l33733e76397a2F423F4n28482BiD6251655n68576D5Ag1347437";

    private static class JWTClaims {
        public static final String ROLE_CLAIM = "role";
        public static final String ID_CLAIM = "id";
    }

    public String generateToken(String userName, Role role, Long id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JWTClaims.ROLE_CLAIM, role);
        claims.put(JWTClaims.ID_CLAIM, id);
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        JwtBuilder token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256);
        if (claims.get(JWTClaims.ROLE_CLAIM) == Role.CLEANING_SERVER)
            token.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 120)); // 2 hour
        else
            token.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)); // 30 min
        return token.compact();
    }

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
