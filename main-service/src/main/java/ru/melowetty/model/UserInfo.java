package ru.melowetty.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public record UserInfo(
        String username,
        List<GrantedAuthority> roles
) {
}
