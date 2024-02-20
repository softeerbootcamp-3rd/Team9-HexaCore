package com.hexacore.tayo.report;

import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.report.dto.CreateReportDto;
import com.hexacore.tayo.report.model.Report;
import com.hexacore.tayo.reservation.ReservationRepository;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReservationRepository reservationRepository;

    public void createReport(CreateReportDto reportDto) {
        Reservation reservation = reservationRepository.findById(reportDto.getReservationId())
                .orElseThrow(() -> new GeneralException(ErrorCode.RESERVATION_NOT_FOUND));

        Long requestedUserId = reportDto.getRequestedUserId();

        // 신고한 사용자가 예약의 호스트가 아닌 경우
        if (!requestedUserId.equals(reservation.getHost().getId())) {
            throw new GeneralException(ErrorCode.REPORTED_BY_OTHERS);
        }

        // Using 상태가 아닌 예약에 신고를 하는 경우
        if (reservation.getStatus() != ReservationStatus.USING) {
            throw new GeneralException(ErrorCode.REPORTED_NOT_USING_RESERVATION);
        }

        Report report = Report.builder()
                .reservation(reservation)
                .content(reportDto.getContent())
                .build();

        reportRepository.save(report);
    }
}
