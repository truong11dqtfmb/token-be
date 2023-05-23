package com.dqt.token.dtos.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtDto {

    private String token;
    private String refreshToken;

    public static JwtDto of(String token, String refreshToken) {
        return new JwtDto(token, refreshToken);
    }

}