package com.hexacore.tayo.auth.jwt;

import com.hexacore.tayo.auth.jwt.RefreshTokenRepository;
import com.hexacore.tayo.auth.jwt.model.RefreshToken;
import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 디비에 리프레시 토큰 저장
     *
     * @param userId
     * @param token
     */
    public void saveRefreshToken(Long userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .id(userId)
                .build();

        // 생성한 리프레시 토큰을 데이터 베이스에 저장
        refreshTokenRepository.save(refreshToken);
    }

    /**
     * 디비에 저장되어 있는 리프레시 토큰 삭제
     *
     * @param userId 리프레시 토큰을 발급받은 주체
     */
    @Transactional
    public void deleteRefreshToken(Long userId) {
        refreshTokenRepository.deleteById(userId);
    }

    /**
     * 디비에서 userId로 리프레시 토큰을 조회
     *
     * @param userId 리프레시 토큰을 발급받은 주체
     * @return 디비에서 조회한 리프레시 토큰
     */
    public String getRefreshToken(Long userId) {
        RefreshToken refreshToken = refreshTokenRepository.findById(userId).orElseThrow(() ->
                new AuthException(ErrorCode.INVALID_JWT_TOKEN));

        return refreshToken.getToken();
    }
}
