package com.hexacore.tayo.user.dto;

import com.hexacore.tayo.user.model.User;
import jakarta.validation.constraints.NotNull;
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

    @SuppressWarnings("checkstyle:NeedBraces")
    public GetUserSimpleResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.profileImgUrl = (user.getProfileImgUrl() != null) ? user.getProfileImgUrl()
                : "https://hexacore-bucket.s3.ap-northeast-2.amazonaws.com/defaultProfile.png";
        this.phoneNumber = user.getPhoneNumber();
    }

}
