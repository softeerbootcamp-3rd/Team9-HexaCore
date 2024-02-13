package com.hexacore.tayo.reservation;

import com.hexacore.tayo.reservation.model.ReservationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findAllByHost_id(Long hostId);

    List<ReservationEntity> findAllByGuest_id(Long guestId);
}
