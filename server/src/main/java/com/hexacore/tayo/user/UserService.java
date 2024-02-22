package com.hexacore.tayo.user;

import com.hexacore.tayo.car.CarRepository;
import com.hexacore.tayo.car.dto.GetCarResponseDto;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.car.model.CarDateRange;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.user.dto.GetUserInfoResponseDto;
import com.hexacore.tayo.user.dto.GetUserPaymentInfoResponseDto;
import com.hexacore.tayo.user.dto.UpdateUserBillingKeyRequestDto;
import com.hexacore.tayo.user.dto.UpdateUserRequestDto;
import com.hexacore.tayo.user.model.User;
import com.hexacore.tayo.util.Encryptor;
import com.hexacore.tayo.util.S3Manager;
import com.hexacore.tayo.util.payment.PaymentManager;
import com.hexacore.tayo.util.payment.TossPaymentDto.TossBilling;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final S3Manager s3Manager;
    private final PaymentManager paymentManager;

    @Transactional
    public void update(Long userId, UpdateUserRequestDto updateRequestDto) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() ->
                new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 시용자 프로필 이미지 수정시 - s3에서 원래 이미지 삭제 후 새로 업로드
        if (updateRequestDto.getProfileImg() != null && !updateRequestDto.getProfileImg().isEmpty()) {
            if (user.getProfileImgUrl() != null) {
                s3Manager.deleteImage(user.getProfileImgUrl());
            }
            String newProfileImgUrl = s3Manager.uploadImage(updateRequestDto.getProfileImg());
            user.setProfileImgUrl(newProfileImgUrl);
        }

        // 새로운 비밀번호를 입력한 경우
        if (updateRequestDto.getPassword() != null && !updateRequestDto.getPassword().isEmpty()) {
            user.setPassword(Encryptor.encryptPwd(updateRequestDto.getPassword()));
        }

        // 새로운 전화번호를 입력한 경우
        if (updateRequestDto.getPhoneNumber() != null && !updateRequestDto.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(updateRequestDto.getPhoneNumber());
        }
    }

    public GetUserInfoResponseDto getUser(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() ->
                new GeneralException(ErrorCode.USER_NOT_FOUND));

        return getUserInfo(user);
    }

    public GetCarResponseDto getUserCar(Long userId) {
        Car car = carRepository.findByOwner_IdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_CAR_NOT_EXISTS));
        return new GetCarResponseDto(car, getCarAvailableDatesForHost(car.getCarDateRanges()));
    }

    private GetUserInfoResponseDto getUserInfo(User user) {
        return GetUserInfoResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

    public GetUserPaymentInfoResponseDto getUserPaymentInfo(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        return GetUserPaymentInfoResponseDto.of(user);
    }

    @Transactional
    public void updateUserBillingKey(Long userId, UpdateUserBillingKeyRequestDto updateUserBillingKeyRequestDto) {
        // 빌링 키 발급
        TossBilling billingResponse = paymentManager.requestBillingKey(updateUserBillingKeyRequestDto.getCustomerKey(),
                updateUserBillingKeyRequestDto.getAuthKey());
        // 유저의 빌링 키 업데이트
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        user.setBillingKey(billingResponse.getBillingKey());
    }

    private List<List<String>> getCarAvailableDatesForHost(List<CarDateRange> carDateRanges) {
        List<List<String>> carAvailableDates = new ArrayList<>();

        for (CarDateRange carDateRange : carDateRanges) {
            LocalDate start = carDateRange.getStartDate();
            LocalDate end = carDateRange.getEndDate();

            // 만약 end가 현재 날짜보다 전일 경우 추가하지 않음
            if (end.isBefore(LocalDate.now())) {
                continue;
            }

            // 만약 start가 현재 날짜보다 전일 경우 start를 현재 날짜로 업데이트
            if (start.isBefore(LocalDate.now())) {
                start = LocalDate.now();
            }

            carAvailableDates.add(List.of(start.toString(), end.toString()));
        }

        return carAvailableDates.stream().sorted(Comparator.comparing(list -> list.get(0)))
                .collect(Collectors.toList());
    }
}
