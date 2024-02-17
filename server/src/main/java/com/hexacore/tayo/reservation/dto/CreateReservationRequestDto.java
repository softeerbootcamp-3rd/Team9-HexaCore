package com.hexacore.tayo.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CreateReservationRequestDto {

    @NotNull(message = "carId가 null이어서는 안됩니다.")
    private final Long carId;

    @NotNull(message = "rentDateTime이 null이어서는 안됩니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime rentDateTime;

    @NotNull(message = "returnDateTime이 null이어서는 안됩니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime returnDateTime;
}
