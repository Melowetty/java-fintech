package ru.melowetty.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.melowetty.model.ChangePasswordToken;
import ru.melowetty.service.PasswordRecoveryService;
import ru.melowetty.validation.annotation.ValidPassword;

@RequiredArgsConstructor
@RestController
@RequestMapping("auth/password-recovery")
public class PasswordRecoveryController {
    private final PasswordRecoveryService passwordRecoveryService;

    @PostMapping
    public ChangePasswordToken initPasswordRecovery(@RequestBody @Valid InitPasswordRecoveryRequest request) {
        return passwordRecoveryService.initPasswordRecovery(request.username);
    }

    @PostMapping("/auth-code")
    public ChangePasswordToken authCodeInput(@RequestBody @Valid AuthCodePasswordRecoveryRequest request) {
        return passwordRecoveryService.validateAuthCode(request.token, request.authCode);
    }

    @PostMapping("/auth-code-resend")
    public ChangePasswordToken resendAuthCode(@RequestBody @Valid ResendAuthCodePasswordRecoveryRequest request) {
        return passwordRecoveryService.reSendAuthCode(request.token);
    }

    @PostMapping("/new-password")
    public void changePassword(@RequestBody @Valid ChangingPasswordPasswordRecoveryRequest request) {
        passwordRecoveryService.changePassword(request.token, request.newPassword);
    }


    record InitPasswordRecoveryRequest(
            @NotBlank(message = "Имя пользователя не может быть пустым!")
            String username
    ) {
    }

    record ResendAuthCodePasswordRecoveryRequest(
            @NotBlank(message = "Токен смены пароля не может быть пустым!")
            String token
    ) {

    }

    record AuthCodePasswordRecoveryRequest(
            @NotBlank(message = "Токен смены пароля не может быть пустым!")
            String token,
            @NotBlank(message = "Код подтверждения не может быть пустым!")
            String authCode
    ) {
    }

    record ChangingPasswordPasswordRecoveryRequest(
            @NotBlank(message = "Токен смены пароля не может быть пустым!")
            String token,
            @ValidPassword
            String newPassword
    ) {
    }
}
