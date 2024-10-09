package ua.edu.ukma.cleaning.user.server;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import ua.edu.ukma.cleaning.user.Role;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class UserServerJwtService {
    public String secret = "53c7566B597l33733e76397a2F423F4n28482BiD6251655n68576D5Ag1347437";

    private class JWTClaims {
        public static String ROLE_CLAIM = "role";
        public static String ID_CLAIM = "id";
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
