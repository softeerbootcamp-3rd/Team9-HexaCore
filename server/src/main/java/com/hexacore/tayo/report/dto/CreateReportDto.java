package com.hexacore.tayo.report.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateReportDto {

    Long requestedUserId;
    Long reservationId;
    String content;
}
