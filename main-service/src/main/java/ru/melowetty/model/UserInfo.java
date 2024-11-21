package ru.melowetty.model;

import java.util.List;

public record UserInfo(
        String username,
        List<UserRole> roles
) {
}
