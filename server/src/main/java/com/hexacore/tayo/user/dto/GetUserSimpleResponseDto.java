package com.hexacore.tayo.user.dto;

<<<<<<<< HEAD:server/src/main/java/com/hexacore/tayo/user/dto/GetUserSimpleResponseDto.java
import com.hexacore.tayo.user.model.User;
========
import com.hexacore.tayo.user.model.UserEntity;
>>>>>>>> dev:server/src/main/java/com/hexacore/tayo/user/dto/UserSimpleDto.java
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
