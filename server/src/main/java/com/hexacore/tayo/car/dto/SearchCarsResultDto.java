package com.hexacore.tayo.car.dto;

import com.hexacore.tayo.car.model.Car;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchCarsResultDto {

    private final Long id;
    private final String subcategory;
    private final String imageUrl;
    private final String address;
    private final Double mileage;
    private final Integer capacity;
    private final Integer feePerHour;

    static public SearchCarsResultDto of(Car car) {
        return SearchCarsResultDto.builder()
                .id(car.getId())
                .subcategory(car.getSubcategory().getName())
                .imageUrl(car.getCarImages().get(0).getUrl())
                .address(car.getAddress())
                .mileage(car.getMileage())
                .capacity(car.getCapacity())
                .feePerHour(car.getFeePerHour())
                .build();
    }

    @Override
    public String toString() {
        return "SearchCarsResultDto{"
                + "id=" + id
                + ", subcategory='" + subcategory + '\''
                + ", imageUrl='" + imageUrl + '\''
                + ", address='" + address + '\''
                + ", mileage=" + mileage
                + ", capacity=" + capacity
                + ", feePerHour=" + feePerHour
                + '}';
    }
}
