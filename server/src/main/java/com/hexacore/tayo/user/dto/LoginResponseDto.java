package com.hexacore.tayo.user.dto;

import com.hexacore.tayo.auth.jwt.dto.GetTokenResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    private GetTokenResponseDto tokenResponseDto;
    private GetUserInfoResponseDto loginUserInfo;

}
