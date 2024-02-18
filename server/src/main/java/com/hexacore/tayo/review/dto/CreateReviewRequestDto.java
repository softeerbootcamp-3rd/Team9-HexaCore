package com.hexacore.tayo.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
public class CreateReviewRequestDto {

    @NotNull(message = "reservationId가 null 이어서는 안됩니다.")
    private Long reservationId;

    @NotBlank(message = "리뷰 내용이 null 이어서는 안됩니다.")
    private String contents;

    @NotNull(message = "별점이 null 이어서는 안됩니다.")
    @Range(max = 5, message = "별점은 0 ~ 5 사이의 정수값이어야 합니다.")
    private Integer rate;
}
