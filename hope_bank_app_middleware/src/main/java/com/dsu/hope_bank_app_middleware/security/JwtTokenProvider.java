package com.dsu.hope_bank_app_middleware.security;

import com.dsu.hope_bank_app_middleware.exception.CustomAPIException;
import com.dsu.hope_bank_app_middleware.security.entity.User;
import com.dsu.hope_bank_app_middleware.security.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {
    private final UserRepository userRepository;

    public JwtTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration-ms:900000}")
    private long accessTokenExpirationMs;

    public String generateToken(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new CustomAPIException(HttpStatus.BAD_REQUEST, "User not found: " + authentication.getName()));
        return generateAccessToken(user);
    }

    public String generateAccessToken(User user) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + accessTokenExpirationMs);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("first_name", user.getFirst_name())
                .claim("last_name", user.getLast_name())
                .claim("customer_number", user.getCustomer_number())
                .claim("mustChangePassword", user.isMustChangePassword())
                .claim("env", user.getEnv())
                .claim("email", user.getEmail())
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMs / 1000;
    }

    private Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    public String getUsername(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Date getExpirationDate(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "JWT claims string is empty.");
        }

    }

    /**
     * For logout: accept a still-valid or expired access JWT (signature verified).
     * Returns empty if the string is not a JWT (e.g. opaque refresh token mistakenly sent as Bearer).
     */
    public Optional<Claims> parseAccessTokenClaimsAllowingExpired(String token) {
        try {
            return Optional.of(Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody());
        } catch (ExpiredJwtException ex) {
            return Optional.of(ex.getClaims());
        } catch (MalformedJwtException | IllegalArgumentException ex) {
            return Optional.empty();
        } catch (JwtException ex) {
            throw new CustomAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        }
    }
}
