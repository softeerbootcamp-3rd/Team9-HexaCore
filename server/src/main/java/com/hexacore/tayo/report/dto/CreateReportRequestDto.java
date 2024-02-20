package com.hexacore.tayo.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateReportRequestDto {

    @NotNull
    Long reservationId;
    @NotNull
    String content;
}
