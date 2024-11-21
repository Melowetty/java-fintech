package ru.melowetty.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.melowetty.entity.User;
import ru.melowetty.model.AccessToken;
import ru.melowetty.model.AuthInfo;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AccessToken registerUser(String username, String password) {
        var user = userService.createUser(username, password);
        var token = jwtService.generateToken(user, false);

        return new AccessToken(token);
    }

    public AccessToken login(String username, String password, boolean rememberMe) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var token = jwtService.generateToken(user, rememberMe);

        return new AccessToken(token);
    }

    public void logout() {
        var details = (AuthInfo) SecurityContextHolder.getContext().getAuthentication().getDetails();
        var token = details.token();
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        jwtService.revokeToken(token);
    }
}
