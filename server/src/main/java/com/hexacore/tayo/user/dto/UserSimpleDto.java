package com.hexacore.tayo.user.dto;

import com.hexacore.tayo.user.model.UserEntity;
import lombok.Getter;

@Getter
public class UserSimpleDto {
    private String name;
    private String profileImgUrl;

    public UserSimpleDto(UserEntity user) {
        this.name = user.getName();
        this.profileImgUrl = user.getProfileImgUrl();
    }

}
