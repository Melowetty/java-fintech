package ru.melowetty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melowetty.entity.RevokeToken;

@Repository
public interface RevokeTokenRepository extends JpaRepository<RevokeToken, String> {
}
