package com.dqt.token.repositories;

import com.dqt.token.entities.Role;
import com.dqt.token.entities.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);

}
