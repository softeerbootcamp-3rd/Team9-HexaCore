package com.hexacore.tayo.car.model;

import jakarta.persistence.criteria.Predicate;

import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CarSpecifications {

    public static Specification<Car> searchCars(SearchCarsDto searchCarsDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

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

            // TODO: car.dates 에서 rentDate, returnDate 를 이용한 필터링
//            if (searchCarsDto.getRentDate() != null && searchCarsDto.getReturnDate() != null) {
                /*
                SELECT *
                FROM test
                JOIN JSON_TABLE(
                        test.dates,
                        "$[*]" COLUMNS(
                        start DATE PATH "$[0]",
                        end DATE PATH "$[1]"
                        )
                    ) AS date
                WHERE
                    (date.start BETWEEN '2024-01-03' AND '2024-01-05' OR date.end BETWEEN '2024-01-03' AND '2024-01-05')
                    OR ('2024-01-03' BETWEEN date.start AND date.end OR '2024-01-05' BETWEEN date.start AND date.end);
                 */
//            }

            if (searchCarsDto.getPeople() > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("capacity"), searchCarsDto.getPeople()));
            }

            if (searchCarsDto.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), searchCarsDto.getType()));
            }

            if (searchCarsDto.getModel() != null) {
                predicates.add(criteriaBuilder.equal(root.get("model").get("category"), searchCarsDto.getModel()));
            }

            if (searchCarsDto.getMinPrice() > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("feePerHour"), searchCarsDto.getMinPrice()));
            }
            if (searchCarsDto.getMaxPrice() > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("feePerHour"), searchCarsDto.getMaxPrice()));
            }

            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
