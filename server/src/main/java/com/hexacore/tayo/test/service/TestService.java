package com.hexacore.tayo.test.service;

import com.hexacore.tayo.common.DataResponseDto;
import com.hexacore.tayo.common.PageInfoDto;
import com.hexacore.tayo.common.PageResponseDto;
import com.hexacore.tayo.common.ResponseCode;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.test.dto.TestDto;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public DataResponseDto<TestDto> testDataResponse() {
        return DataResponseDto.of(new TestDto("hello World!"), "테스트 성공!");
    }

    public ResponseDto testResponse() {
        return ResponseDto.success(ResponseCode.OK, "성공!");
    }

    public DataResponseDto<TestDto> testException() {
        throw new GeneralException(ResponseCode.UNAUTHORIZED, "로그인이 필요한 서비스 입니다");
    }

    public PageResponseDto<TestDto> testPage() {
        return PageResponseDto.of(new TestDto("pagination test!"), new PageInfoDto(1, 5, 10L, 2));
    }
}
