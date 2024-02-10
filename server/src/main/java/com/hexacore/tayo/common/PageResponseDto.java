package com.hexacore.tayo.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PageResponseDto<T> extends ResponseDto {

    private final PageInfoDto pageInfo;

    private final T data;

    private PageResponseDto(T data, PageInfoDto pageInfo) {
        super(true, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
        this.pageInfo = pageInfo;
        this.data = data;
    }

    public static <T> PageResponseDto<T> of(T data, PageInfoDto pageInfoDto) {
        return new PageResponseDto<>(data, pageInfoDto);
    }

    public static <T> PageResponseDto<T> empty() {
        return new PageResponseDto<>(null, null);
    }
}

