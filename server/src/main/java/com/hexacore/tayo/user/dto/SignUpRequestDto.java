package com.hexacore.tayo.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {

    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
            message = "이메일 형식에 맞게 입력해주세요.")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$",
            message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 6자 이상의 비밀번호여야 합니다.")
    private String password;

    private String name;

    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$",
            message = "000-0000-0000 형식에 맞게 입력해주세요.")
    private String phoneNumber;

    private MultipartFile profileImg;

}
