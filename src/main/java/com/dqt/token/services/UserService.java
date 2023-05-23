package com.dqt.token.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dqt.token.dtos.request.SignUpForm;
import com.dqt.token.entities.Role;
import com.dqt.token.entities.RoleName;
import com.dqt.token.entities.User;
import com.dqt.token.exceptions.ResourceNotFoundException;
import com.dqt.token.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheManager cacheManager;


    @Value("${jwt.access.secret}")
    private String jwtSecret;


    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Cacheable(key = "#id", value = "User")
    public User findById(Long id) {
        log.info("Get User from Database!");
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id + ""));
    }

    @CachePut(value = "User", key = "#id")
    public User updateEmail(String email, Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id + ""));

        user.setEmail(email);

        log.info("Update Email from Database!");

        return this.userRepository.save(user);
    }


    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User signUp(SignUpForm signUpForm) {
        User user = new User(signUpForm.getName(), signUpForm.getEmail(), passwordEncoder.encode(signUpForm.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleService.findByName(RoleName.USER).orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", "role"));
        roles.add(userRole);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Cache getCacheByName(String cacheName) {
        return cacheManager.getCache(cacheName);
    }
}
