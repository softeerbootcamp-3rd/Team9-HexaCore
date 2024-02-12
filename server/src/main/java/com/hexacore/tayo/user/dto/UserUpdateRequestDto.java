package com.hexacore.tayo.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class UserUpdateRequestDto {

    private String password;
    private String phoneNumber;
    private MultipartFile profileImg;

}
