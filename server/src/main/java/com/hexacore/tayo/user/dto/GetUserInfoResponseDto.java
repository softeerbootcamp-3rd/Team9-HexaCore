package com.hexacore.tayo.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserInfoResponseDto {

    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImgUrl;

    @Override
    public String toString() {
        return "GetUserInfoResponseDto{"
                + "userId=" + userId
                + ", name='" + name + '\''
                + ", email='" + email + '\''
                + ", phoneNumber='" + phoneNumber + '\''
                + ", profileImgUrl='" + profileImgUrl + '\''
                + '}';
    }
}
