package com.hexacore.tayo.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDto {

    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImgUrl;

}
