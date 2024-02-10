package com.hexacore.tayo.filter;

import com.hexacore.tayo.common.ResponseCode;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.AuthException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * 사용자 인증 필터 로직을 진행하는 동안 발생하는 예외를 처리해주는 필터
 */
public class ErrorHandlingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        try {
            chain.doFilter(request, response);
        } catch (AuthException e) {
            // AuthException 처리
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(e.getErrorCode().getHttpStatus().value());
            httpResponse.getWriter().write(ResponseDto.error(e.getErrorCode()).toString());
        } catch (Exception e) {
            // 그 밖의 예외 처리
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpResponse.getWriter().write(ResponseDto.error(ResponseCode.INTERNAL_SERVER_ERROR, e).toString());
        }
    }
}
