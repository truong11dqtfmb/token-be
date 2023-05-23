package com.dqt.token.services;

import com.dqt.token.entities.Role;
import com.dqt.token.entities.RoleName;
import com.dqt.token.exceptions.ResourceNotFoundException;
import com.dqt.token.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;


    public Optional<Role> findByName(RoleName name) {
        return Optional.ofNullable(roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", name.toString())));
    }
}
