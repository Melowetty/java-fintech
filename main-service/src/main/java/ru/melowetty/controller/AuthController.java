package ru.melowetty.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.melowetty.controller.request.LoginRequest;
import ru.melowetty.controller.request.RegisterRequest;
import ru.melowetty.model.AccessToken;
import ru.melowetty.service.AuthService;

@RestController
@RequiredArgsConstructor
@Valid
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AccessToken registerUser(@RequestBody RegisterRequest request) {
        return authService.registerUser(request.getUsername(), request.getPassword());
    }

    @PostMapping("/auth")
    public AccessToken loginUser(@RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password(), request.rememberMe());
    }

    @PostMapping("/logout")
    public void logout() {
       authService.logout();
    }
}
