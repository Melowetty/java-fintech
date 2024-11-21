package ru.melowetty.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.melowetty.entity.RevokeToken;
import ru.melowetty.entity.User;
import ru.melowetty.model.UserInfo;
import ru.melowetty.repository.RevokeTokenRepository;
import ru.melowetty.repository.UserRepository;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${spring.security.jwt.private-key}")
    private String privateKey;
    private final RevokeTokenRepository revokeTokenRepository;
    private final UserRepository userRepository;

    public UserInfo extractUserInfo(String token) {
        var claims = getClaims(token);
        var username = claims.get("username", String.class);
        List<String> roles = claims.get("roles", List.class);
        List<GrantedAuthority> authorities = roles.stream().map((authority) ->
                (GrantedAuthority) new SimpleGrantedAuthority(authority)).toList();
        return new UserInfo(username, authorities);
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
        var claims = Map.of("username", user.getUsername(),
                "roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(rememberMe
                        ? new Date(System.currentTimeMillis() + Duration.ofDays(30).toMillis())
                        : new Date(System.currentTimeMillis() + Duration.ofMinutes(10).toMillis()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        UserInfo userInfo = extractUserInfo(token);
        var isRevoked = revokeTokenRepository.existsById(token);
        var userIsExist = userRepository.findUserByUsername(userInfo.username()).isPresent();
        return  userIsExist
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
