package com.hexacore.tayo.reservation.model;

import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.common.BaseTime;
import com.hexacore.tayo.user.model.User;
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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToOne
    @JoinColumn(name = "car_date_range_id", nullable = false)
    private CarDateRange carDateRange;

    @Column(name = "rent_date", nullable = false)
    private LocalDateTime rentDateTime;

    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDateTime;

    @Column(name = "fee", nullable = false)
    private Integer fee;

    @Column(name = "status", nullable = false)
    @Setter
    private ReservationStatus status;

}
