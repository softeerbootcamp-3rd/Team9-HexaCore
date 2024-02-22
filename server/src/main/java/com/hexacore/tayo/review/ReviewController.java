package com.hexacore.tayo.review;

import com.hexacore.tayo.common.response.Response;
import com.hexacore.tayo.review.dto.CreateReviewRequestDto;
import com.hexacore.tayo.review.dto.GetCarReviewsResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /* 게스트의 차량에 대한 리뷰 등록 */
    @PostMapping("/car")
    public ResponseEntity<Response> createReviewForCar(HttpServletRequest request,
            @Valid @RequestBody CreateReviewRequestDto createReviewRequestDto) {
        Long writerId = (Long) request.getAttribute("userId");
        reviewService.createReview(writerId, createReviewRequestDto, true);
        return Response.of(HttpStatus.CREATED);
    }

    /* 호스트의 게스트에 대한 리뷰 등록 */
    @PostMapping("/guest")
    public ResponseEntity<Response> createReviewForGuest(HttpServletRequest request,
            @Valid @RequestBody CreateReviewRequestDto createReviewRequestDto) {
        Long writerId = (Long) request.getAttribute("userId");
        reviewService.createReview(writerId, createReviewRequestDto, false);
        return Response.of(HttpStatus.CREATED);
    }

    /* 차량에 대한 리뷰 조회 */
    @GetMapping("/car/{carId}")
    public ResponseEntity<Response> getCarReviews(@PathVariable Long carId, Pageable pageable) {
        Page<GetCarReviewsResponseDto> carReviews = reviewService.getCarReviews(carId, pageable);
        return Response.of(HttpStatus.OK, carReviews);
    }

}
