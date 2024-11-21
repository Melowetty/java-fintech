package ru.melowetty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.melowetty.entity.RevokeToken;

public interface RevokeTokenRepository extends JpaRepository<RevokeToken, String> {
}
