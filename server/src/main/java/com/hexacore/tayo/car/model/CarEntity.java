package com.hexacore.tayo.car.model;

import com.hexacore.tayo.user.model.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import org.antlr.v4.runtime.misc.Pair;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "Car")
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private ModelEntity model;

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
    private List<Pair<Date, Date>> dates;

    @PrePersist
    private void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = new Date();
    }
}
