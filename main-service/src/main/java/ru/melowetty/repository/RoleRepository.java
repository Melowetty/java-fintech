package ru.melowetty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melowetty.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
