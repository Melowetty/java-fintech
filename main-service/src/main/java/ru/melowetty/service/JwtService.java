package ru.melowetty.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.melowetty.entity.RevokeToken;
import ru.melowetty.entity.User;
import ru.melowetty.model.UserInfo;
import ru.melowetty.model.UserRole;
import ru.melowetty.repository.RevokeTokenRepository;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${spring.security.jwt.private-key}")
    private String privateKey;
    private RevokeTokenRepository revokeTokenRepository;

    public UserInfo extractUserInfo(String token) {
        var claims = getClaims(token);
        var username = claims.get("username", String.class);
        List<UserRole> roles = claims.get("roles", List.class);
        return new UserInfo(username, roles);
    }

    public void revokeToken(String token) {
        RevokeToken revokeToken = RevokeToken.builder()
                .token(token)
                .deleteAfter(extractExpirationDate(token))
                .build();

        revokeTokenRepository.save(revokeToken);
    }

    public LocalDateTime extractExpirationDate(String token) {
        Claims claims = getClaims(token);
        return claims
                .getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public String generateToken(
            User user,
            boolean rememberMe
    ) {
        var rolesById = new HashMap<Long, UserRole>();
        for (var role : UserRole.values()) {
            rolesById.put(role.getId(), role);
        }

        return Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(rememberMe
                        ? new Date(System.currentTimeMillis() + Duration.ofDays(30).toMillis())
                        : new Date(System.currentTimeMillis() + Duration.ofMinutes(10).toMillis()))
                .setClaims(Map.of(
                        "username", user.getUsername(),
                        "roles", user.getAuthorities().stream().map(rolesById::get)
                ))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        UserInfo userInfo = extractUserInfo(token);
        var username = userInfo.username();
        var isRevoked = revokeTokenRepository.existsById(token);
        return username != null
                && username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && !isRevoked;
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token)
                .isBefore(LocalDateTime.now());
    }

    private Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] decoded = Decoders.BASE64.decode(privateKey);
        return Keys.hmacShaKeyFor(decoded);
    }
}
