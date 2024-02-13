package com.hexacore.tayo.auth;

import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.user.dto.LoginRequestDto;
import com.hexacore.tayo.user.dto.LoginResponseDto;
import com.hexacore.tayo.user.dto.SignUpRequestDto;
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
@RequestMapping("/auth")
public class AuthController {
    @Value("${jwt.refresh.cookie-name}")
    private String refreshTokenCookieName;

    @Value("${jwt.access.cookie-name}")
    private String accessTokenCookieName;

    private static final String accessTokenPath = "/";
    private static final String refreshTokenPath = "/auth/refresh";

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signUp(@ModelAttribute SignUpRequestDto signUpRequestDto) {
        ResponseDto response = authService.signUp(signUpRequestDto);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);

        response.addCookie(makeTokenCookie(accessTokenCookieName, loginResponseDto.getAccessToken(), accessTokenPath));
        response.addCookie(makeTokenCookie(refreshTokenCookieName, loginResponseDto.getRefreshToken(), refreshTokenPath));

        ResponseDto responseDto = DataResponseDto.of(loginResponseDto.getLoginUserInfo());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<Void> logOut(HttpServletRequest request, HttpServletResponse response) {
        authService.logOut((Long) request.getAttribute("userId"));
        resetCookie(response);

        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/users")
    public ResponseEntity<ResponseDto> deleteUser(HttpServletRequest request, HttpServletResponse response) {
        authService.delete((Long) request.getAttribute("userId"));
        resetCookie(response);

        return ResponseEntity.ok().build();
    }

    // 엑세스 토큰 재발급
    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        String newAccessToken = authService.refresh((Long) request.getAttribute("userId"));
        response.addCookie(makeTokenCookie(accessTokenCookieName, newAccessToken, accessTokenPath));

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

    private void resetCookie(HttpServletResponse response) {
        Cookie accessToken = makeTokenCookie(accessTokenCookieName, "", accessTokenPath);
        accessToken.setMaxAge(0);
        Cookie refreshToken = makeTokenCookie(refreshTokenCookieName, "", refreshTokenPath);
        refreshToken.setMaxAge(0);

        response.addCookie(accessToken);
        response.addCookie(refreshToken);
    }
}
