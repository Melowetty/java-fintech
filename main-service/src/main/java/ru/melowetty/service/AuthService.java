package ru.melowetty.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.melowetty.exception.UsernameAlreadyExists;
import ru.melowetty.model.AccessToken;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AccessToken registerUser(String username, String password) {
        if (userService.usernameIsExist(username)) {
            throw new UsernameAlreadyExists("Пользователь с таким логином уже есть!");
        }

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

        var user = userService.getUserByUsername(username);
        var token = jwtService.generateToken(user, rememberMe);

        return new AccessToken(token);
    }

    public void logout() {
        var token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        jwtService.revokeToken(token);
    }
}
