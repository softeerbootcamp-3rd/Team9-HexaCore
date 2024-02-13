package com.hexacore.tayo.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {

    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private MultipartFile profileImg;

}
