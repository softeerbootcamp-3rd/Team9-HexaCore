package com.hexacore.tayo.auth.jwt.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTokenResponseDto {

    private String accessToken;
    private String refreshToken;

    @Override
    public String toString() {
        return "GetTokenResponseDto{"
                + "accessToken='" + accessToken + '\''
                + ", refreshToken='" + refreshToken + '\''
                + '}';
    }
}
