package com.hexacore.tayo.car.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

@Getter
@Setter
public class SearchCarsRequestDto {

    @NotNull(message = "거리 값이 필요합니다.")
    @Positive(message = "거리 값은 양수여야 합니다.")
    private Double distance; // unit: meters

    @NotNull(message = "위도 값이 필요합니다.")
    @Range(min = -90, max = 90, message = "위도 값은 -90 이상, 90 이하입니다.")
    private Double lat;

    @NotNull(message = "경도 값이 필요합니다.")
    @Range(min = -180, max = 180, message = "경도 값은 -180 초과, 180 이하입니다.")
    private Double lng;

    @NotNull(message = "시작일이 필요합니다.")
    private LocalDate startDate;

    @NotNull(message = "종료일이 필요합니다.")
    private LocalDate endDate;

    @Positive(message = "인원 수는 양수여야 합니다.")
    private Integer party;

    private String type;

    private Long categoryId;

    private Long subcategoryId;

    @Positive(message = "가격은 양수여야 합니다.")
    private Integer minPrice;

    @Positive(message = "가격은 양수여야 합니다.")
    private Integer maxPrice;

    @Override
    public String toString() {
        return "SearchCarsRequestDto{" +
                "distance=" + distance +
                ", position=" + lat + ", " + lng +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", party=" + party +
                ", type=" + type +
                ", categoryId=" + categoryId +
                ", subcategoryId=" + subcategoryId +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                '}';
    }
}
