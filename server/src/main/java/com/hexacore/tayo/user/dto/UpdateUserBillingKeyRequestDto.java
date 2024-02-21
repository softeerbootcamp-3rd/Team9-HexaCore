package com.hexacore.tayo.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateUserBillingKeyRequestDto {

    @NotNull
    private String customerKey;
    @NotNull
    private String authKey;
}
