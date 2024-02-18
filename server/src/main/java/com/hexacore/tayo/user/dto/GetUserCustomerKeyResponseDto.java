package com.hexacore.tayo.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserCustomerKeyResponseDto {
    private String customerKey;
    private String name;
}
