package com.hexacore.tayo.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hexacore.tayo.common.errors.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor
public class CommonResponse {
    private final Boolean success;
    private final Integer code;
    private final String message;

    @JsonInclude(Include.NON_NULL)
    private final PageInfoDto pageInfo;

    @JsonInclude(Include.NON_NULL)
    private final Object data;

    // HttpStatus에 대한 응답
    public static ResponseEntity<CommonResponse> response(HttpStatus status) {
        return data(status, null);
    }

    // ErrorCode에 대한 응답
    public static ResponseEntity<CommonResponse> error(ErrorCode errorCode) {
        return error(errorCode, null);
    }

    public static ResponseEntity<CommonResponse> error(ErrorCode errorCode, String message) {
        return new ResponseEntity<>(
                new CommonResponse(false, errorCode.getCode(), errorCode.getErrorMessage(message), null, null),
                errorCode.getHttpStatus()
        );
    }

    // 데이터에 대한 응답
    public static ResponseEntity<CommonResponse> data(HttpStatus status, Object data) {
        return new ResponseEntity<>(
                new CommonResponse(true, status.value(), status.getReasonPhrase(), null, data),
                status
        );
    }

    // 페이지네이션에 대한 응답
    public static ResponseEntity<CommonResponse> pagination(HttpStatus status, Page<?> page) {
        return new ResponseEntity<>(
                new CommonResponse(true, status.value(), status.getReasonPhrase(),
                        new PageInfoDto(page.getNumber() + 1, page.getSize(), page.getTotalElements(),
                                page.getTotalPages()), page.getContent()),
                status
        );
    }
}
