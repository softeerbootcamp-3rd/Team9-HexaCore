package com.hexacore.tayo.test;

import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.PageInfoDto;
import com.hexacore.tayo.common.PageResponseDto;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.test.model.TestDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public DataResponseDto<TestDto> testDataResponse() {
        return DataResponseDto.of(new TestDto("hello World!"));
    }

    public ResponseDto testResponse() {
        return ResponseDto.success(HttpStatus.OK);
    }

    public DataResponseDto<TestDto> testException() {
        throw new GeneralException(ErrorCode.USER_UNAUTHORIZED);
    }

    public PageResponseDto<TestDto> testPage() {
        return PageResponseDto.of(new TestDto("pagination test!"), new PageInfoDto(1, 5, 10L, 2));
    }
}
