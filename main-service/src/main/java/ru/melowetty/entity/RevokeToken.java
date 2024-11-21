package ru.melowetty.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "revoke_token")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevokeToken {
    @Id
    private String token;

    @Column(nullable = false)
    public LocalDateTime deleteAfter;
}
