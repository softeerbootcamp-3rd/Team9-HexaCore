package com.hexacore.tayo.reservation.model;

import com.hexacore.tayo.car.model.CarEntity;
import com.hexacore.tayo.common.BaseTime;
import com.hexacore.tayo.user.model.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Reservation")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private CarEntity car;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private UserEntity guest;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private UserEntity host;

    @Column(name = "rent_date", nullable = false)
    private LocalDateTime rentDate;

    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDate;

    @Column(name = "status", nullable = false)
    @Setter
    private ReservationStatus status;
}
