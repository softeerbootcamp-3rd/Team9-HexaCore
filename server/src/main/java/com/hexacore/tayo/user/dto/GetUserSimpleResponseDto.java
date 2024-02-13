package com.hexacore.tayo.user.dto;

import com.hexacore.tayo.user.model.User;
import lombok.Getter;

@Getter
public class GetUserSimpleResponseDto {

    private String name;
    private String profileImg;

    public GetUserSimpleResponseDto(User user) {
        this.name = user.getName();
        this.profileImg = user.getProfileImg();
    }

}
