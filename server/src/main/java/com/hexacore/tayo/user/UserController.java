package com.hexacore.tayo.user;

import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.user.dto.LoginRequestDto;
import com.hexacore.tayo.user.dto.LoginResponseDto;
import com.hexacore.tayo.user.dto.SignUpRequestDto;
import com.hexacore.tayo.user.dto.UserUpdateRequestDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    @Value("${jwt.refresh.cookie-name}")
    private String refreshTokenCookieName;

    @Value("${jwt.access.cookie-name}")
    private String accessTokenCookieName;

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signUp(@ModelAttribute SignUpRequestDto signUpRequestDto) {
        ResponseDto response = userService.signUp(signUpRequestDto);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // 유저 정보 수정
    @PutMapping("/users")
    public ResponseEntity<ResponseDto> updateUser(HttpServletRequest request, @ModelAttribute UserUpdateRequestDto updateRequestDto) {
        ResponseDto response = userService.update(Long.valueOf((Integer) request.getAttribute("userId")), updateRequestDto);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = userService.login(loginRequestDto);

        response.addCookie(makeTokenCookie(accessTokenCookieName, loginResponseDto.getAccessToken(), "/"));
        response.addCookie(makeTokenCookie(refreshTokenCookieName, loginResponseDto.getRefreshToken(), "/refresh"));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        String newAccessToken = userService.refresh((Long) request.getAttribute("userId"));
        response.addCookie(makeTokenCookie(accessTokenCookieName, newAccessToken, "/"));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/login/test")
    public ResponseEntity<String[]> loginTest(HttpServletRequest request) {
        String[] arr = new String[2];
        arr[0] = String.valueOf(request.getAttribute("userId"));
        arr[1] = (String) request.getAttribute("userName");

        return new ResponseEntity<>(arr, HttpStatusCode.valueOf(200));
    }

    private Cookie makeTokenCookie(String cookieName, String token, String path) {
        Cookie tokenCookie = new Cookie(cookieName, token);
        tokenCookie.setPath(path);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);

        return tokenCookie;
    }
}
