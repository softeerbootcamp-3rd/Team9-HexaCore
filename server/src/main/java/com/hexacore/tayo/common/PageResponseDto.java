package com.hexacore.tayo.common;

import lombok.Getter;

@Getter
public class PageResponseDto<T> extends ResponseDto {

    private final PageInfoDto pageInfo;

    private final T data;

    private PageResponseDto(T data, PageInfoDto pageInfo) {
        super(true, ResponseCode.OK.getCode(), ResponseCode.OK.getMessage());
        this.pageInfo = pageInfo;
        this.data = data;
    }

    private PageResponseDto(T data, PageInfoDto pageInfo, String message) {
        super(true, ResponseCode.OK.getCode(), message);
        this.pageInfo = pageInfo;
        this.data = data;

    }

    public static <T> PageResponseDto<T> of(T data, PageInfoDto pageInfoDto) {
        return new PageResponseDto<>(data, pageInfoDto);
    }

    public static <T> PageResponseDto<T> of(T data, PageInfoDto pageInfoDto, String message) {
        return new PageResponseDto<>(data, pageInfoDto, message);
    }

    public static <T> PageResponseDto<T> empty() {
        return new PageResponseDto<>(null, null);
    }
}

