package com.hexacore.tayo.car.model;

import com.hexacore.tayo.common.BaseTime;
import com.hexacore.tayo.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Car")
public class Car extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "subCategory_id", nullable = false)
    private SubCategory subCategory;

    @Column(name = "car_number", nullable = false)
    private String carNumber;

    @Column(name = "mileage")
    private Double mileage;

    @Column(name = "fuel")
    private String fuel;

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

    @Column(name = "position", nullable = false)
    private Point position;

    @Column(name = "description")
    private String description;

    @Column(name = "dates", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<List<Date>> dates;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "car")
    @Builder.Default
    private List<Image> images = new ArrayList<>();
}
