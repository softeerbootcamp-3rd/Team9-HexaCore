package com.hexacore.tayo.user;

import com.hexacore.tayo.auth.JwtService;
import com.hexacore.tayo.auth.RefreshTokenRepository;
import com.hexacore.tayo.auth.model.RefreshTokenEntity;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.image.S3Manager;
import com.hexacore.tayo.user.UserRepository;
import com.hexacore.tayo.user.dto.LoginRequestDto;
import com.hexacore.tayo.user.dto.LoginResponseDto;
import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.user.dto.SignUpRequestDto;
import com.hexacore.tayo.user.model.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final S3Manager s3Manager;

    @Transactional
    public ResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        // 프로필 사진 업로드
        String profileUrl = s3Manager.uploadImage(signUpRequestDto.getProfileImg());

        UserEntity newUser = UserEntity.builder()
                .email(signUpRequestDto.getEmail())
                .name(signUpRequestDto.getName())
                .password(encryptPwd(signUpRequestDto.getPassword()))
                .phoneNumber(signUpRequestDto.getPhoneNumber())
                .profileImgUrl(profileUrl)
                .build();

        userRepository.save(newUser);
        return ResponseDto.success(HttpStatus.CREATED);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        UserEntity loginUser = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() ->
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
        UserEntity expiredUser = userRepository.findById(userId).orElseThrow(() ->
                new AuthException(ErrorCode.USER_NOT_FOUND));

        return jwtService.createAccessToken(expiredUser);
    }

    // 비밀번호 암호화
    private String encryptPwd(String plainPwd) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(plainPwd, salt);
    }
}
