package com.hexacore.tayo.config;

import com.hexacore.tayo.auth.interceptor.AuthenticationInterceptor;
import com.hexacore.tayo.auth.interceptor.RefreshAccessTokenInterceptor;
import com.hexacore.tayo.auth.jwt.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {
    private final JwtParser jwtParser;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/signup", "/auth/login", "/auth/refresh"); // 로그인 인증 필요없는 요청

        // 엑세스 토큰 재발급 요청인 경우
        registry.addInterceptor(refreshAccessTokenInterceptor())
                .order(2)
                .addPathPatterns("/auth/refresh");
    }

    @Bean
    public RefreshAccessTokenInterceptor refreshAccessTokenInterceptor() {
        return new RefreshAccessTokenInterceptor(jwtParser);
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(jwtParser);
    }
}
