package com.hexacore.tayo.user;

import com.hexacore.tayo.auth.JwtService;
import com.hexacore.tayo.user.dto.LoginRequestDto;
import com.hexacore.tayo.user.dto.LoginResponseDto;
import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User loginUser = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() ->
                new AuthException(ErrorCode.USER_NOT_FOUND));

        if (!loginRequestDto.getPassword().equals(loginUser.getPassword())) {
            throw new AuthException(ErrorCode.USER_WRONG_PASSWORD);
        }

        return LoginResponseDto.builder()
                .accessToken(jwtService.createAccessToken(loginUser))
                .refreshToken(jwtService.createRefreshToken(loginUser.getId()))
                .build();
    }

    public String refresh(Long userId) {
        User expiredUser = userRepository.findById(userId).orElseThrow(() ->
                new AuthException(ErrorCode.USER_NOT_FOUND));

        return jwtService.createAccessToken(expiredUser);
    }
}
