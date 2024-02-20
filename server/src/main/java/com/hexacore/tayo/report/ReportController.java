package com.hexacore.tayo.report;

import com.hexacore.tayo.common.response.Response;
import com.hexacore.tayo.report.dto.CreateReportDto;
import com.hexacore.tayo.report.dto.CreateReportRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Response> createReport(HttpServletRequest request,
            @Valid @RequestBody CreateReportRequestDto requestDto) {
        Long userId = (Long) request.getAttribute("userId");
        CreateReportDto reportDto = CreateReportDto.builder()
                .requestedUserId(userId)
                .content(requestDto.getContent())
                .reservationId(requestDto.getReservationId())
                .build();

        reportService.createReport(reportDto);

        return Response.of(HttpStatus.OK);
    }
}
