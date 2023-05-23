package com.dqt.token;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
@EnableCaching
public class TokenApplication {


    public static void main(String[] args) {
        System.out.println("ALO ALO 500 anh em!");
        log.info("500 anh em alo!");

        SpringApplication.run(TokenApplication.class, args);
    }


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
