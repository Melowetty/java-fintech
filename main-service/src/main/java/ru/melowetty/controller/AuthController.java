package ru.melowetty.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.melowetty.controller.request.LoginRequest;
import ru.melowetty.controller.request.RegisterRequest;
import ru.melowetty.model.AccessToken;
import ru.melowetty.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AccessToken registerUser(@RequestBody @Valid RegisterRequest request) {
        return authService.registerUser(request.getUsername(), request.getPassword());
    }

    @PostMapping("/login")
    public AccessToken loginUser(@RequestBody @Valid LoginRequest request) {
        return authService.login(request.getUsername(), request.getPassword(), request.isRememberMe());
    }

    @PostMapping("/logout")
    public void logout() {
        authService.logout();
    }
}
