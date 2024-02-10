package com.hexacore.tayo.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DataResponseDto<T> extends ResponseDto {

    private final T data;

    private DataResponseDto(T data) {
        super(true, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
        this.data = data;
    }

    public static <T> DataResponseDto<T> of(T data) {
        return new DataResponseDto<>(data);
    }

    public static <T> DataResponseDto<T> empty() {
        return new DataResponseDto<>(null);
    }
}
