package com.hexacore.tayo.config;

import com.hexacore.tayo.filter.ErrorHandlingFilter;
import com.hexacore.tayo.filter.JwtAuthenticationFilter;
import com.hexacore.tayo.filter.RefreshAccessTokenFilter;
import com.hexacore.tayo.jwt.JwtService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<ErrorHandlingFilter> ErrorHandlingFilter() {
        FilterRegistrationBean<ErrorHandlingFilter> errorHandlingFilterFilterBean = new FilterRegistrationBean<>();
        errorHandlingFilterFilterBean.setFilter(new ErrorHandlingFilter());
        errorHandlingFilterFilterBean.addUrlPatterns("/*");
        errorHandlingFilterFilterBean.setOrder(1);
        return errorHandlingFilterFilterBean;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> JwtAuthenticationFilter(JwtService jwtService) {
        FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterBean = new FilterRegistrationBean<>();
        jwtAuthenticationFilterBean.setFilter(new JwtAuthenticationFilter(jwtService));
        jwtAuthenticationFilterBean.addUrlPatterns("/*");
        jwtAuthenticationFilterBean.setOrder(2);
        return jwtAuthenticationFilterBean;
    }

    @Bean
    public FilterRegistrationBean<RefreshAccessTokenFilter> RefreshAccessTokenFilter(JwtService jwtService) {
        FilterRegistrationBean<RefreshAccessTokenFilter> refreshAccessTokenFilterBean = new FilterRegistrationBean<>();
        refreshAccessTokenFilterBean.setFilter(new RefreshAccessTokenFilter(jwtService));
        refreshAccessTokenFilterBean.addUrlPatterns("/auth/refresh");
        refreshAccessTokenFilterBean.setOrder(2);
        return refreshAccessTokenFilterBean;
    }
}
