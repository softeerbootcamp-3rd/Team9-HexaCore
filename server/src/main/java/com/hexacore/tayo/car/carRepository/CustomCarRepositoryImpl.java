package com.hexacore.tayo.car.carRepository;

import com.hexacore.tayo.car.dto.SearchCarsDto;
import com.hexacore.tayo.car.dto.SearchCarsResultDto;
import com.hexacore.tayo.car.model.CarType;
import com.hexacore.tayo.car.model.QCar;
import com.hexacore.tayo.car.model.QCarDateRange;
import com.hexacore.tayo.car.model.QCarImage;
import com.hexacore.tayo.category.model.QSubcategory;
import com.hexacore.tayo.common.Position;
import com.hexacore.tayo.reservation.model.QReservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;


@RequiredArgsConstructor
public class CustomCarRepositoryImpl implements CustomCarRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<SearchCarsResultDto> search(SearchCarsDto searchCondition, Pageable pageable) {
        QCar car = QCar.car;
        QCarImage carImage = QCarImage.carImage;
        QCarDateRange carDateRange = QCarDateRange.carDateRange;
        QReservation reservation = QReservation.reservation;
        QSubcategory subcategory = QSubcategory.subcategory;

        Expression<String> subcategoryName = ExpressionUtils.as(
                JPAExpressions.select(subcategory.name)
                .from(subcategory)
                .where(subcategory.id.eq(car.subcategory.id))
                , "subcategory");

        Expression<String> imageUrlSubQuery = ExpressionUtils.as(
                JPAExpressions.select(carImage.url)
                        .from(carImage)
                        .where(
                                carImage.car.id.eq(car.id),
                                carImage.orderIdx.eq(0)
                        )
                        .limit(1),
                "imageUrl");

        List<SearchCarsResultDto> results = queryFactory
                .select(Projections.fields(SearchCarsResultDto.class,
                        car.id,
                        subcategoryName,
                        imageUrlSubQuery,
                        car.address,
                        car.mileage,
                        car.capacity,
                        car.feePerHour
                ))
                .from(car)
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
                .groupBy(car)
                .orderBy(car.id.desc()) // TODO: pagable.getSort()를 이용하여 정렬 조건을 추가
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();


        boolean hasNext = results.size() > pageable.getPageSize();
        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new SliceImpl<>(results, pageable, hasNext);
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
