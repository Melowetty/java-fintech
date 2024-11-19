package ru.melowetty.model;

import lombok.Getter;

public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    @Getter
    private String userRole;

    private UserRole(String userRole) {
        this.userRole = userRole;
    }
}
