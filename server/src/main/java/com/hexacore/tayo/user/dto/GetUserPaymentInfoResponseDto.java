package com.hexacore.tayo.user.dto;

import com.hexacore.tayo.user.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserPaymentInfoResponseDto {
    private String customerKey;
    private String name;
    private String billingKey;

    static public GetUserPaymentInfoResponseDto of(User user) {
        return GetUserPaymentInfoResponseDto.builder()
                .customerKey(user.getCustomerKey())
                .name(user.getName())
                .billingKey(user.getBillingKey())
                .build();
    }
}
