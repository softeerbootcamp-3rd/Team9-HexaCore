package com.hexacore.tayo.car;

import com.hexacore.tayo.car.dto.SearchCarsDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.model.QCar;
import com.hexacore.tayo.car.model.QCarDateRange;
import com.hexacore.tayo.common.Position;
import com.hexacore.tayo.reservation.model.QReservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;


@RequiredArgsConstructor
public class CarCustomRepositoryImpl implements CarCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Car> search(SearchCarsDto searchCondition, Pageable pageable) {
        QCar car = QCar.car;
        QCarDateRange carDateRange = QCarDateRange.carDateRange;
        QReservation reservation = QReservation.reservation;

        JPAQuery<Car> query = queryFactory
                .selectFrom(car)
                .join(car.carDateRanges, carDateRange)
                .leftJoin(car.reservations, reservation)
                .on(reservation.status.in(ReservationStatus.READY, ReservationStatus.USING))
                .where(
                        isCarInCircle(searchCondition.getPosition(), searchCondition.getDistance()),
                        car.isDeleted.isFalse(),
                        carDateRange.startDate.loe(searchCondition.getStartDate()),
                        carDateRange.endDate.goe(searchCondition.getEndDate()),
                        reservation.isNull()
                                .or(reservation.returnDateTime.lt(searchCondition.getStartDate().atStartOfDay()))
                                .or(reservation.rentDateTime.gt(searchCondition.getEndDate().atStartOfDay())),

                        optionalCarTypeEq(searchCondition.getType()),
                        optionalCategoryEq(searchCondition.getCategoryId(), searchCondition.getSubcategoryId()),
                        optionalCapacityGoe(searchCondition.getParty()),
                        optionalFeePerHourInRange(searchCondition.getMinPrice(), searchCondition.getMaxPrice())
                )
                .groupBy(car);

        List<Car> cars = query
                .orderBy(car.id.desc()) // TODO: pagable.getSort()를 이용하여 정렬 조건을 추가
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = query.fetch().size();

        return new PageImpl<>(cars, pageable, total);
    }

    private BooleanExpression isCarInCircle(Position center, Double radius) {
        return Expressions.booleanTemplate(
                "ST_Contains(ST_Buffer(ST_GeomFromText({0}, {1}), {2}), {3})",
                center.toWKT(),
                Position.SRID,
                radius,
                QCar.car.position
        );
    }

    private BooleanExpression optionalCarTypeEq(CarType type) {
        if (type != null) {
            return QCar.car.type.eq(type);
        }
        return null;
    }

    private BooleanExpression optionalCategoryEq(Long categoryId, Long subcategoryId) {
        if (subcategoryId != null) {
            return QCar.car.subcategory.id.eq(subcategoryId);
        } else if (categoryId != null) {
            return QCar.car.subcategory.category.id.eq(categoryId);
        }
        return null;
    }

    private BooleanExpression optionalCapacityGoe(Integer min) {
        if (min != null) {
            return QCar.car.capacity.goe(min);
        }
        return null;
    }

    private BooleanExpression optionalFeePerHourInRange(Integer minPrice, Integer maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return QCar.car.feePerHour.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return QCar.car.feePerHour.goe(minPrice);
        } else if (maxPrice != null) {
            return QCar.car.feePerHour.loe(maxPrice);
        }
        return null;
    }
}
