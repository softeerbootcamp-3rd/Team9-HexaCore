package com.hexacore.tayo.user.dto;

import com.hexacore.tayo.user.model.User;
import lombok.Getter;

@Getter
public class GetUserSimpleResponseDto {

    private String name;
    private String profileImgUrl;

    public GetUserSimpleResponseDto(User user) {
        this.name = user.getName();
        this.profileImgUrl = user.getProfileImgUrl();
    }

}
