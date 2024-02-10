package com.hexacore.tayo.auth;

import com.hexacore.tayo.auth.dto.LoginRequestDto;
import com.hexacore.tayo.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;

//    @PostMapping("/login")
//    public ResponseEntity<ResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
//
//    }
}
