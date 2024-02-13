package com.hexacore.tayo.user;

import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.user.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // 유저 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto> getUserInfo(@PathVariable Long userId) {
        UserInfoResponseDto userInfoDto = userService.getUser(userId);

        ResponseDto responseDto = DataResponseDto.of(userInfoDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 유저 정보 수정
    @PutMapping
    public ResponseEntity<ResponseDto> updateUser(HttpServletRequest request, @ModelAttribute UpdateUserRequestDto updateRequestDto) {
        ResponseDto response = userService.update((Long) request.getAttribute("userId"), updateRequestDto);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}
