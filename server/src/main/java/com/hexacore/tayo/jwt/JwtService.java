package com.hexacore.tayo.jwt;

import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.user.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret-key}")
    private static String secretKey;
    private final Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

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

    public String createRefreshToken(String userId, Long id) {
        String refreshToken = Jwts.builder()
                .setSubject(userId) // 토큰을 발급한 주체
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // todo 리프레시 토큰 디비에 저장

        return refreshToken;
    }

    /**
     * @param token
     * @return 서명을 이용해 토큰을 검증하고 토큰을 파싱해 토큰에 저장된 값을 읽어온다.
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key) // 서명을 검증할 키
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            // 서명 검증에 실패한 경우
            throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }
}
