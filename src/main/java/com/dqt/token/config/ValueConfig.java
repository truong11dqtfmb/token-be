//package com.dqt.token.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class ValueConfig {
//
//    @Value("${jwt.access.secret}")
//    public String jwtSecret;
//
//    @Value("${jwt.refresh.secret}")
//    public String jwtRefreshSecret;
//
//    @Value("${jwt.access.expired}")
//    public Long jwtExpiration;
//
//    @Value("${jwt.refresh.expired}")
//    public Long jwtRefreshExpiration;
//
//    @Bean
//    public Map<String, Object> MyValue(){
//        Map<String, Object> map = new HashMap<>();
//        map.put("jwtSecret", jwtSecret);
//        map.put("jwtRefreshSecret", jwtRefreshSecret);
//        map.put("jwtExpiration", jwtExpiration);
//        map.put("jwtRefreshExpiration", jwtRefreshExpiration);
//        return map;
//    }
//
//
//}
