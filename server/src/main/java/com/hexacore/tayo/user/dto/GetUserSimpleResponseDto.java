package com.hexacore.tayo.user.dto;

import com.hexacore.tayo.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetUserSimpleResponseDto {

    private Long id;
    private String name;
    private String profileImgUrl;
    private String phoneNumber;

    public GetUserSimpleResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.profileImgUrl = user.getProfileImgUrl();
        this.phoneNumber = user.getPhoneNumber();
    }

}
