package com.hexacore.tayo.review;

import com.hexacore.tayo.common.response.Response;
import com.hexacore.tayo.review.dto.CreateReviewForCarRequestDto;
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
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /* 차량에 대한 리뷰 등록 */
    @PostMapping("/car")
    public ResponseEntity<Response> createReviewForCar(HttpServletRequest request,
            @Valid @RequestBody CreateReviewForCarRequestDto createReviewForCarRequestDto) {
        Long writerId = (Long) request.getAttribute("userId");
        reviewService.createReviewForCar(writerId, createReviewForCarRequestDto);
        return Response.of(HttpStatus.CREATED);
    }

}
