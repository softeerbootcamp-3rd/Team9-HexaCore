package com.hexacore.tayo.reservation;

import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByHost_id(Long hostId);

    List<Reservation> findAllByGuest_id(Long guestId);

    List<Reservation> findAllByCar_idAndStatusInOrderByRentDateTimeAsc(Long carId, List<ReservationStatus> statusList);
}
