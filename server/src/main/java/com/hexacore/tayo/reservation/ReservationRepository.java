package com.hexacore.tayo.reservation;

import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByHost_idOrderByStatusAscRentDateTimeAsc(Long hostId);

    Page<Reservation> findAllByGuest_idOrderByStatusAscRentDateTimeAsc(Long guestId, Pageable pageable);

    List<Reservation> findAllByCar_idAndStatusInOrderByRentDateTimeAsc(Long carId, List<ReservationStatus> statusList);

}
