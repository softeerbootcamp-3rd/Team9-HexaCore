package com.hexacore.tayo.auth.jwt.util;

import com.hexacore.tayo.auth.jwt.RefreshTokenService;
import com.hexacore.tayo.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.secret-key}")
    private String secretKey;

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
    public String createAccessToken(User user) {
        Claims claims = Jwts.claims();
        claims.put("userId", user.getId());
        claims.put("userName", user.getName());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰을 발급한 시간
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod)) // 토큰 만료 일자
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // 토큰 서명
                .compact();
    }

    /**
     * 리프레시 토큰 발급하고 디비에 저장 - 토큰에는 userId만 저장
     *
     * @param userId 리프레시 토큰을 발급받은 주체
     * @return 발급한 리프레시 토큰
     */
    @Transactional
    public String createRefreshToken(Long userId) {
        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(userId)) // 토큰을 발급한 주체
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationPeriod))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        refreshTokenService.saveRefreshToken(userId, refreshToken);

        return refreshToken;
    }
}
