package com.hexacore.tayo.config;

import com.hexacore.tayo.interceptor.AuthenticationInterceptor;
import com.hexacore.tayo.interceptor.RefreshAccessTokenInterceptor;
import com.hexacore.tayo.auth.jwt.util.JwtParser;
import com.hexacore.tayo.interceptor.LoggingInterceptor;
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
        // 요청에 대한 로깅
        registry.addInterceptor(loggingAspect())
                .order(1);

        registry.addInterceptor(authenticationInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/signup", "/auth/login", "/auth/refresh") // 로그인 인증 필요없는 요청
                .excludePathPatterns("cars/{carId}");
        // 엑세스 토큰 재발급 요청인 경우
        registry.addInterceptor(refreshAccessTokenInterceptor())
                .order(3)
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

    @Bean
    public LoggingInterceptor loggingAspect() {
        return new LoggingInterceptor();
    }
}
