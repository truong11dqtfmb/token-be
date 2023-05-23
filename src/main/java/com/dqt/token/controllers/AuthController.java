package com.dqt.token.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dqt.token.customUser.CustomUserDetail;
import com.dqt.token.dtos.request.ChangeEmailDTO;
import com.dqt.token.dtos.request.RefreshTokenDTO;
import com.dqt.token.dtos.request.SignInForm;
import com.dqt.token.dtos.request.SignUpForm;
import com.dqt.token.dtos.response.ApiResponse;
import com.dqt.token.entities.User;
import com.dqt.token.services.RoleService;
import com.dqt.token.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    private HttpServletResponse httpServletResponse;

    //    Get Value từ application.properties
    @Value("${jwt.access.secret}")
    public String jwtSecret;

    @Value("${jwt.refresh.secret}")
    public String jwtRefreshSecret;

    @Value("${jwt.access.expired}")
    public Long jwtExpiration;

    @Value("${jwt.refresh.expired}")
    public Long jwtRefreshExpiration;


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> singup(@RequestBody SignUpForm signUpForm) {
        ApiResponse apiResponse = new ApiResponse();


        if (userService.existsByEmail(signUpForm.getEmail())) {
            apiResponse.setStatus(false);
            apiResponse.setMessage("The email existed! Please try again");
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
        User user = this.userService.signUp(signUpForm);
        apiResponse.setStatus(true);
        apiResponse.setMessage("Create Account successfully!");
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> singin(@RequestBody SignInForm signInForm, HttpServletRequest request) throws JsonProcessingException {
        User u = userService.findByEmail(signInForm.getEmail());
        User user = userService.findById(u.getId());

//      Xác thực người dùng(Email,password):
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInForm.getEmail(), signInForm.getPassword()));

//      Mã hóa jwtSecret & jwtRefreshSecret
        Algorithm algorithmAccessToken = Algorithm.HMAC256(jwtSecret.getBytes());
        Algorithm algorithmRefreshToken = Algorithm.HMAC256(jwtRefreshSecret.getBytes());

//      Tạo access_token & refresh_token
        String access_token = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration))
//                .withIssuer(request.getRequestURL().toString())
//                .withClaim("roles", user.getRoles().stream().collect(Collectors.toList()))
                .sign(algorithmAccessToken);
        String refresh_token = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
                .sign(algorithmRefreshToken);

//      return API
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        tokens.put("email", user.getEmail());
        tokens.put("roles", user.getRoles().stream().collect(Collectors.toList()));

        String json = new ObjectMapper().writeValueAsString(tokens);
        return new ResponseEntity<>(json, OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDTO refreshToken) throws IOException {
        try {

            String refresh_token = refreshToken.getRefreshToken();

            Algorithm algorithmAccess = Algorithm.HMAC256(jwtSecret.getBytes());
            Algorithm algorithmRefresh = Algorithm.HMAC256(jwtRefreshSecret.getBytes());

//          Verify & DecodeJWT   => Email
            JWTVerifier verifier = JWT.require(algorithmRefresh).build();
            DecodedJWT decodedJWT = verifier.verify(refresh_token);
            String email = decodedJWT.getSubject();

            User u = userService.findByEmail(email);
            User user = userService.findById(u.getId());

            CustomUserDetail customUserDetail = CustomUserDetail.build(user);

//          Tạo access_token mới
            String access_token = JWT.create()
                    .withSubject(user.getEmail())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
//                        .withIssuer(request.getRequestURL().toString())
//                        .withClaim("roles", user.getRoles().stream().collect(Collectors.toList()))
                    .sign(algorithmAccess);


//          Return Api
            Map<String, Object> tokens = new HashMap<>();
            tokens.put("access_token", access_token);
            tokens.put("refresh_token", refresh_token);
            tokens.put("email", customUserDetail.getUsername());
            tokens.put("roles", customUserDetail.getAuthorities().stream().collect(Collectors.toList()));

            String json = new ObjectMapper().writeValueAsString(tokens);

            return new ResponseEntity<>(json, OK);


        } catch (Exception exception) {

            log.info("loi truy cap vao refresh token");
            String error = exception.getMessage();
            return new ResponseEntity<>(error, UNAUTHORIZED);
        }
    }

//    API get cache by name
    @GetMapping("/cache")
    public Cache getCache(@RequestParam("cacheName") String cacheName) {
        return this.userService.getCacheByName(cacheName);
    }


    @PostMapping("/changeEmail")
    public ResponseEntity<?> changeEmail(HttpServletRequest request, @RequestBody ChangeEmailDTO changeEmailDTO) {
        ApiResponse apiResponse = new ApiResponse();

//      Get email từ header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        try {
            String token = authorizationHeader.substring("Bearer ".length());
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String emailToken = decodedJWT.getSubject();
            User u = this.userService.findByEmail(emailToken);


//          Update email từ emailToken vừa get được từ header
            User user = this.userService.updateEmail(changeEmailDTO.getEmail(), u.getId());

            log.info("Update User: " + user);

//          return Api
            apiResponse.setStatus(true);
            apiResponse.setMessage("Update Email Account successfully!");
            apiResponse.setData(user);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);

        } catch (Exception e) {
            log.info(e.getMessage());
        }
//        }
        return new ResponseEntity<>("Error", BAD_REQUEST);

    }


//    @PostMapping("/refresh")
//    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String authorizationHeader = request.getHeader(AUTHORIZATION);
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            try {
//                String refresh_token = authorizationHeader.substring("Bearer ".length());
//                Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
//                Algorithm algorithmAccess = Algorithm.HMAC256(jwtSecret.getBytes());
//                Algorithm algorithmRefresh = Algorithm.HMAC256(jwtRefreshSecret.getBytes());
//                JWTVerifier verifier = JWT.require(algorithmRefresh).build();
//                DecodedJWT decodedJWT = verifier.verify(refresh_token);
//                String email = decodedJWT.getSubject();
//
//                User user = userService.findByEmail(email);
//
////                org.springframework.security.core.userdetails.User useroke = user;
//
//                CustomUserDetail customUserDetail = CustomUserDetail.build(user);
//
//
////                List<Role> rolesss =  customUserDetail.getAuthorities().stream().map(Role ->{
////                    String e =  com.dqt.token.entities.Role.class.getName();
////                    RoleName roleName = RoleName.valueOf(String.valueOf(e));
////
////                    String name = roleName.name();
////
////                    return name;
////                }).collect(Collectors.toList());
//
//                String access_token = JWT.create()
//                        .withSubject(user.getEmail())
//                        .withExpiresAt(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
////                        .withIssuer(request.getRequestURL().toString())
////                        .withClaim("roles", user.getRoles().stream().collect(Collectors.toList()))
//                        .sign(algorithmAccess);
//
//
//
//
////                .withClaim("roles", customUserDetail.getAuthorities().stream().collect(Collectors.toList()).toString())
//
////                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
//
//
//                Map<String, String> tokens = new HashMap<>();
//                tokens.put("access_token", access_token);
//                tokens.put("refresh_token", refresh_token);
//                tokens.put("email", customUserDetail.getUsername());
//                tokens.put("roles", customUserDetail.getAuthorities().stream().collect(Collectors.toList()).toString());
//
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
//            } catch (Exception exception) {
//                response.setHeader("error", exception.getMessage());
//                response.setStatus(FORBIDDEN.value());
//                //response.sendError(FORBIDDEN.value());
//                Map<String, String> error = new HashMap<>();
//                error.put("error_message", exception.getMessage());
//                log.info("loi truy cap vao refresh token");
//                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
//                new ObjectMapper().writeValue(response.getOutputStream(), error);
//            }
//        } else {
//            throw new RuntimeException("Refresh token is missing");
//        }
//    }


}


