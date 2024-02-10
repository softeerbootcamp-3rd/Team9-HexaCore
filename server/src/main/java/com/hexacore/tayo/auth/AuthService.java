package com.hexacore.tayo.auth;

import com.hexacore.tayo.auth.dto.LoginRequestDto;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.user.model.UserEntity;
import com.hexacore.tayo.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
}
