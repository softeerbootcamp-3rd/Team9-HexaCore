package com.hexacore.tayo.user.dto;

import com.hexacore.tayo.user.model.UserEntity;
import lombok.Getter;

@Getter
public class UserSimpleDto {
    private String name;
    private String profileImg;

    public UserSimpleDto(UserEntity user) {
        this.name = user.getName();
        this.profileImg = user.getProfileImg();
    }

}
