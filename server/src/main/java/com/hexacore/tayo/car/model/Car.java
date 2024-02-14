package com.hexacore.tayo.car.model;

import com.hexacore.tayo.category.model.Subcategory;
import com.hexacore.tayo.common.BaseTime;
import com.hexacore.tayo.user.model.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "car", indexes = {
        @Index(name = "car_position_idx", columnList = "position", unique = false),
})
public class Car extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "subcategory_id", nullable = false)
    private Subcategory subcategory;

    @Column(name = "car_number", nullable = false)
    private String carNumber;

    @Column(name = "mileage")
    private Double mileage;

    @Column(name = "fuel")
    private FuelType fuel;

    @Column(name = "type", nullable = false)
    private CarType type;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "year")
    private Integer year;

    @Column(name = "fee_per_hour", nullable = false)
    private Integer feePerHour;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "position", nullable = false, columnDefinition = "POINT SRID 4326")
    private Point position;

    @Column(name = "description")
    private String description;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "car")
    @Builder.Default
    private List<CarImage> carImages = new ArrayList<>();

    @OneToMany(mappedBy = "car")
    @Builder.Default
    private List<CarDateRange> carDateRanges = new ArrayList<>();
}
