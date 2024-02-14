package com.hexacore.tayo.car.model;

import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.Subcategory;
import com.hexacore.tayo.reservation.model.Reservation;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CarSpecifications {

    public static Specification<Car> searchCars(SearchCarsDto searchCarsDto) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            // 위경도 기반 거리 검색
            if (searchCarsDto.getPosition() != null && searchCarsDto.getDistance() > 0) {
                var bufferExpression = criteriaBuilder.function("ST_Buffer",
                        Polygon.class,
                        criteriaBuilder.literal(searchCarsDto.getPosition().toPoint()),
                        criteriaBuilder.literal(searchCarsDto.getDistance())
                );

                predicates.add(
                        criteriaBuilder.isTrue(
                                criteriaBuilder.function("ST_Contains",
                                        Boolean.class,
                                        bufferExpression,
                                        root.get("position")
                                )
                        )
                );
            }

            // 날짜 기반 예약 가능 차량 검색
            if (searchCarsDto.getStartDate() != null && searchCarsDto.getEndDate() != null && searchCarsDto.getStartDate().isBefore(searchCarsDto.getEndDate())) {
                LocalDate startDate = searchCarsDto.getStartDate();
                LocalDate endDate = searchCarsDto.getEndDate();

                Join<Car, CarDateRange> carDateRangeJoin = root.join("carDateRanges");
                Join<CarDateRange, Reservation> reservationJoin = carDateRangeJoin.join("reservations");

                Predicate dateRangePredicate = criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(carDateRangeJoin.get("startDate"), startDate),
                        criteriaBuilder.greaterThanOrEqualTo(carDateRangeJoin.get("endDate"), endDate)
                );

                Predicate reservationPredicate = criteriaBuilder.or(
                        criteriaBuilder.isNull(reservationJoin.get("id")),
                        criteriaBuilder.greaterThan(reservationJoin.get("rentDateTime"), endDate),
                        criteriaBuilder.lessThan(reservationJoin.get("returnDateTime"), startDate)
                );

                predicates.add(criteriaBuilder.and(dateRangePredicate, reservationPredicate));
            }

            // 인원수 기반 차량 검색
            if (searchCarsDto.getParty() != null && searchCarsDto.getParty() > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("capacity"), searchCarsDto.getParty()));
            }

            // 차량 타입 기반 차량 검색
            if (searchCarsDto.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), searchCarsDto.getType()));
            }

            // 카테고리 기반 차량 검색(하위 카테고리가 있을 경우 하위 카테고리 기반 검색, 없을 경우 상위 카테고리 기반 검색)
            if (searchCarsDto.getSubcategoryId() != null && searchCarsDto.getSubcategoryId() > 0) {
                predicates.add(criteriaBuilder.equal(root.get("subcategory").get("id"), searchCarsDto.getSubcategoryId()));
            }
            else if (searchCarsDto.getCategoryId() > 0) {
                Join<Car, Subcategory> subcategoryJoin = root.join("subcategory");
                Join<Subcategory, Category> categoryJoin = subcategoryJoin.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("id"), searchCarsDto.getCategoryId()));
            }

            // 가격 기반 차량 검색
            if (searchCarsDto.getMinPrice() != null && searchCarsDto.getMinPrice() > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("feePerHour"), searchCarsDto.getMinPrice()));
            }
            if (searchCarsDto.getMaxPrice() != null && searchCarsDto.getMaxPrice() > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("feePerHour"), searchCarsDto.getMaxPrice()));
            }

            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
