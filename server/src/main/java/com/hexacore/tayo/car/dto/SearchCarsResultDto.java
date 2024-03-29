package com.hexacore.tayo.car.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchCarsResultDto {

    private Long id;
    private String subcategory;
    private String imageUrl;
    private String address;
    private Double mileage;
    private Integer capacity;
    private Integer feePerHour;
    private Double averageRate;

    @Override
    public String toString() {
        return "{"
                + "id=" + id
                + ", subcategory='" + subcategory + '\''
                + ", imageUrl='" + imageUrl + '\''
                + ", address='" + address + '\''
                + ", mileage=" + mileage
                + ", capacity=" + capacity
                + ", feePerHour=" + feePerHour
                + ", averageRate=" + averageRate
                + '}';
    }
}
