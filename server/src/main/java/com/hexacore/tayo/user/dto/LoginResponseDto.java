package com.hexacore.tayo.user.dto;

import com.hexacore.tayo.auth.jwt.dto.GetTokenResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    private GetTokenResponseDto tokens;
    private GetUserInfoResponseDto loginUserInfo;

    @Override
    public String toString() {
        return "{"
                + "tokens=" + tokens
                + ", loginUserInfo=" + loginUserInfo
                + '}';
    }
}
