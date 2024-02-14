package com.hexacore.tayo.auth;

import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.common.response.Response;
import com.hexacore.tayo.user.dto.LoginRequestDto;
import com.hexacore.tayo.user.dto.LoginResponseDto;
import com.hexacore.tayo.user.dto.SignUpRequestDto;
import com.hexacore.tayo.user.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Response> signUp(@ModelAttribute SignUpRequestDto signUpRequestDto) {
        User user = authService.signUp(signUpRequestDto);

        if (user == null) {
            throw new GeneralException(ErrorCode.SERVER_ERROR);
        }

        return Response.of(HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);

        response.addCookie(makeTokenCookie(accessTokenCookieName, loginResponseDto.getAccessToken(), accessTokenPath));
        response.addCookie(
                makeTokenCookie(refreshTokenCookieName, loginResponseDto.getRefreshToken(), refreshTokenPath));

        return Response.of(HttpStatus.OK, loginResponseDto.getLoginUserInfo());
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<Response> logOut(HttpServletRequest request, HttpServletResponse response) {
        authService.logOut((Long) request.getAttribute("userId"));
        resetCookie(response);

        return Response.of(HttpStatus.OK);
    }

    // 회원 탈퇴
    @DeleteMapping("/users")
    public ResponseEntity<Response> deleteUser(HttpServletRequest request, HttpServletResponse response) {
        authService.delete((Long) request.getAttribute("userId"));
        resetCookie(response);

        return Response.of(HttpStatus.OK);
    }

    // 엑세스 토큰 재발급
    @GetMapping("/refresh")
    public ResponseEntity<Response> refresh(HttpServletRequest request, HttpServletResponse response) {

        String newAccessToken = authService.refresh((Long) request.getAttribute("userId"));
        response.addCookie(makeTokenCookie(accessTokenCookieName, newAccessToken, accessTokenPath));

        return Response.of(HttpStatus.OK);
    }

    @GetMapping("/login/test")
    public ResponseEntity<Response> loginTest(HttpServletRequest request) {
        String[] arr = new String[2];
        arr[0] = String.valueOf(request.getAttribute("userId"));
        arr[1] = (String) request.getAttribute("userName");

        return Response.of(HttpStatus.OK, arr);
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
