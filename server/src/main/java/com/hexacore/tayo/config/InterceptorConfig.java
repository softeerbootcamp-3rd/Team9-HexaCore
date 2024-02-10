package com.hexacore.tayo.config;

import com.hexacore.tayo.interceptor.AuthenticationInterceptor;
import com.hexacore.tayo.interceptor.RefreshAccessTokenInterceptor;
import com.hexacore.tayo.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {
    private final JwtService jwtService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/member/signUp", "/center/signUp", "/login", "/refresh"); // 로그인 인증 필요없는 요청

        // 엑세스 토큰 재발급 요청인 경우 refreshAccessTokenInterceptor
        registry.addInterceptor(refreshAccessTokenInterceptor())
                .order(2)
                .addPathPatterns("/refresh");
    }

    @Bean
    public RefreshAccessTokenInterceptor refreshAccessTokenInterceptor() {
        return new RefreshAccessTokenInterceptor(jwtService);
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(jwtService);
    }
}
