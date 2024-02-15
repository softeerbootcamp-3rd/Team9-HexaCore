package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.SearchCarsParamsDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.category.model.Category;
import com.hexacore.tayo.category.model.Subcategory;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CarSpecifications {

    public static Specification<Car> searchCars(SearchCarsParamsDto searchCarsParamsDto) {
        if (searchCarsParamsDto == null || !searchCarsParamsDto.isValid())
            throw new GeneralException(ErrorCode.VALIDATION_ERROR);

        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            // 위경도 기반 거리 검색
            if (searchCarsParamsDto.getPosition() != null && searchCarsParamsDto.getDistance() > 0) {
                Point point = searchCarsParamsDto.getPosition().toPointForSpec();
                if (point.getX() < -90 || point.getX() > 90 || point.getY() <= -180 || point.getY() > 180)
                    throw new GeneralException(ErrorCode.INVALID_POSITION);

                var bufferExpression = criteriaBuilder.function("ST_Buffer",
                        Polygon.class,
                        criteriaBuilder.literal(point),
                        criteriaBuilder.literal(searchCarsParamsDto.getDistance())
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
            if (searchCarsParamsDto.getStartDate() != null && searchCarsParamsDto.getEndDate() != null && searchCarsParamsDto.getStartDate().isBefore(searchCarsParamsDto.getEndDate())) {
                LocalDate startDate = searchCarsParamsDto.getStartDate();
                LocalDate endDate = searchCarsParamsDto.getEndDate();

                Join<Car, CarDateRange> carDateRangeJoin = root.join("carDateRanges");
                Join<CarDateRange, Reservation> reservationJoin = carDateRangeJoin.join("reservations", JoinType.LEFT);

                Predicate reservationStatusPredicate = criteriaBuilder.not(
                        criteriaBuilder.or(
                                criteriaBuilder.equal(reservationJoin.get("status"), ReservationStatus.READY.ordinal()),
                                criteriaBuilder.equal(reservationJoin.get("status"), ReservationStatus.USING.ordinal())
                        )
                );

                Predicate dateRangePredicate = criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(carDateRangeJoin.get("startDate"), startDate),
                        criteriaBuilder.greaterThanOrEqualTo(carDateRangeJoin.get("endDate"), endDate)
                );

                Predicate reservationPredicate = criteriaBuilder.or(
                        criteriaBuilder.isNull(reservationJoin.get("id")),
                        criteriaBuilder.greaterThan(reservationJoin.get("rentDateTime"), endDate),
                        criteriaBuilder.lessThan(reservationJoin.get("returnDateTime"), startDate)
                );

                predicates.add(criteriaBuilder.and(
                        reservationStatusPredicate,
                        dateRangePredicate,
                        reservationPredicate
                ));
            }

            // 인원수 기반 차량 검색
            if (searchCarsParamsDto.getParty() != null && searchCarsParamsDto.getParty() > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), searchCarsParamsDto.getParty()));
            }

            // 차량 타입 기반 차량 검색
            if (searchCarsParamsDto.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), searchCarsParamsDto.getType()));
            }

            // 카테고리 기반 차량 검색(하위 카테고리가 있을 경우 하위 카테고리 기반 검색, 없을 경우 상위 카테고리 기반 검색)
            if (searchCarsParamsDto.getSubcategoryId() != null && searchCarsParamsDto.getSubcategoryId() > 0) {
                predicates.add(criteriaBuilder.equal(root.get("subcategory").get("id"), searchCarsParamsDto.getSubcategoryId()));
            }
            else if (searchCarsParamsDto.getSubcategoryId() != null && searchCarsParamsDto.getCategoryId() > 0) {
                Join<Car, Subcategory> subcategoryJoin = root.join("subcategory");
                Join<Subcategory, Category> categoryJoin = subcategoryJoin.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("id"), searchCarsParamsDto.getCategoryId()));
            }

            // 가격 기반 차량 검색
            if (searchCarsParamsDto.getMinPrice() != null && searchCarsParamsDto.getMinPrice() > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("feePerHour"), searchCarsParamsDto.getMinPrice()));
            }
            if (searchCarsParamsDto.getMaxPrice() != null && searchCarsParamsDto.getMaxPrice() > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("feePerHour"), searchCarsParamsDto.getMaxPrice()));
            }

            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
