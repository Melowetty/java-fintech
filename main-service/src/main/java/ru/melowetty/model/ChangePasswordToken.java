package ru.melowetty.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChangePasswordToken {
    public String username;
    public String token;
    public int attempts;
    public boolean isValidated;
    public LocalDateTime nextAttemptTime;
    public LocalDateTime expirationTime;
}
