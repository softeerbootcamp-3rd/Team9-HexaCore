package com.hexacore.tayo.auth;

import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.auth.model.RefreshTokenEntity;
import com.hexacore.tayo.user.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final RefreshTokenRepository refreshTokenRepository;

    //    @Value("${jwt.secret-key}")
    private static String secretKey = "sdfhsfbjsbfhsdbjbsdjhfvsjdhfvsjhvfhjsdfvsdjhfsfsfdsdf";
    private final Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    /**
     * 엑세스 토큰을 발급 - 토큰에 userId 와 userName 을 저장
     *
     * @param user 엑세스 토큰을 요청한 user
     * @return 발급한 엑세스 토큰
     */
    public String createAccessToken(UserEntity user) {
        Claims claims = Jwts.claims();
        claims.put("userId", user.getId());
        claims.put("userName", user.getName());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰을 발급한 시간
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod)) // 토큰 만료 일자
                .signWith(key, SignatureAlgorithm.HS256) // 토큰 서명
                .compact();
    }

    /**
     * 리프레시 토큰 발급하고 디비에 저장 - 토큰에는 userId만 저장
     *
     * @param userId 리프레시 토큰을 발급받은 주체
     * @return 발급한 리프레시 토큰
     */
    public String createRefreshToken(String userId) {
        String refreshToken = Jwts.builder()
                .setSubject(userId) // 토큰을 발급한 주체
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 생성한 리프레시 토큰을 데이터 베이스에 저장
        refreshTokenRepository.save(RefreshTokenEntity.builder()
                .refreshToken(refreshToken)
                .id(Long.valueOf(userId))
                .build()
        );

        return refreshToken;
    }

    /**
     * 디비에 저장되어 있는 리프레시 토큰 삭제
     *
     * @param userId 리프레시 토큰을 발급받은 주체
     */
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
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findById(userId).orElseThrow(() ->
                new AuthException(ErrorCode.INVALID_JWT_TOKEN));

        return refreshTokenEntity.getRefreshToken();
    }

    /**
     * 서명을 이용해 토큰을 검증하고 토큰을 파싱해 토큰에 저장된 값을 읽어온다.
     *
     * @param token 토큰
     * @return Claims
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key) // 서명을 검증할 키
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 서명 검증에 실패한 경우
            throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

    /**
     * 서명으로 토큰의 유효성 검증
     *
     * @param token 토큰
     */
    public void checkTokenValidation(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // 서명을 검증할 키
                    .build()
                    .parseClaimsJws(token);

        } catch (Exception e) {
            // 서명 검증에 실패한 경우
            throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }
}
