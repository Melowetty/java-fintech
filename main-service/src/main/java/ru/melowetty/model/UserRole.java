package ru.melowetty.model;

import lombok.Getter;

public enum UserRole {
    USER(1, "ROLE_USER"),
    ADMIN(2, "ROLE_ADMIN");

    @Getter
    private final long id;

    @Getter
    private final String userRole;

    UserRole(long id, String userRole) {
        this.id = id;
        this.userRole = userRole;
    }
}
