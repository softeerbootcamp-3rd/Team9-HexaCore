package com.hexacore.tayo.auth.jwt.util;

import com.hexacore.tayo.auth.jwt.RefreshTokenService;
import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtParser {

    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    /**
     * 서명을 이용해 토큰을 검증하고 토큰을 파싱해 토큰에 저장된 값을 읽어온다.
     *
     * @param token 토큰
     * @return Claims
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())) // 서명을 검증할 키
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            throw new AuthException(ErrorCode.EXPIRED_JWT_TOKEN);
        } catch (Exception e) {
            // 서명 검증에 실패한 경우
            throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

    /**
     * 디비에 저장된 유저의 리프레시 토큰 값을 가져온 후, 서명으로 리프레시 토큰의 유효성 검증 후 반환
     *
     * @param userId 유저 아이디
     * @return 디비에 저장해 놓은 해당 유저의 리프레시 토큰
     */
    public String getValidRefreshToken(Long userId) {
        String refreshToken = refreshTokenService.getRefreshToken(userId);

        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())) // 서명을 검증할 키
                    .build()
                    .parseClaimsJws(refreshToken);

            return refreshToken;
        } catch (ExpiredJwtException e) {
            // 리프레시 토큰이 만료된 경우
            throw new AuthException(ErrorCode.EXPIRED_JWT_TOKEN);
        } catch (Exception e) {
            // 서명 검증에 실패한 경우
            throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }
}
