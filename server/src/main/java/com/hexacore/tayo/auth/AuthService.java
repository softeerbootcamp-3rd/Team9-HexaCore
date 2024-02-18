package com.hexacore.tayo.auth;

import com.hexacore.tayo.auth.jwt.RefreshTokenService;
import com.hexacore.tayo.auth.jwt.dto.GetTokenResponseDto;
import com.hexacore.tayo.auth.jwt.util.JwtProvider;
import com.hexacore.tayo.car.CarImageRepository;
import com.hexacore.tayo.car.CarRepository;
import com.hexacore.tayo.reservation.model.Reservation;
import com.hexacore.tayo.reservation.model.ReservationStatus;
import com.hexacore.tayo.util.Encryptor;
import com.hexacore.tayo.car.model.Car;
import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.util.S3Manager;
import com.hexacore.tayo.user.UserRepository;
import com.hexacore.tayo.user.dto.LoginRequestDto;
import com.hexacore.tayo.user.dto.LoginResponseDto;
import com.hexacore.tayo.user.dto.SignUpRequestDto;
import com.hexacore.tayo.user.dto.GetUserInfoResponseDto;
import com.hexacore.tayo.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;
    private final S3Manager s3Manager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public User signUp(SignUpRequestDto signUpRequestDto) {
        // 회원탈퇴하지 않은 사용자 중 이메일 중복되는 유저가 있는지 확인
        if (userRepository.existsByEmailAndIsDeletedFalse(signUpRequestDto.getEmail())) {
            throw new GeneralException(ErrorCode.USER_EMAIL_DUPLICATED);
        }

        // 프로필 사진 업로드
        String profileUrl = null;
        if (signUpRequestDto.getProfileImg() != null && !signUpRequestDto.getProfileImg().isEmpty()) {
            profileUrl = s3Manager.uploadImage(signUpRequestDto.getProfileImg());
        }

        User newUser = User.builder()
                .email(signUpRequestDto.getEmail())
                .name(signUpRequestDto.getName())
                .password(Encryptor.encryptPwd(signUpRequestDto.getPassword()))
                .phoneNumber(signUpRequestDto.getPhoneNumber())
                .profileImgUrl(profileUrl)
                .build();

        return userRepository.save(newUser);
    }

    @Transactional
    public void logOut(Long userId) {
        refreshTokenService.deleteRefreshToken(userId);
    }

    @Transactional
    public void delete(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() ->
                new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 현재 예약한 차량이 있는 경우
        if (isOnGoing(user.getReservations())) {
            throw new GeneralException(ErrorCode.USER_HAS_RESERVATION);
        }

        // 유저가 등록한 차가 있으면
        Car userCar = carRepository.findByOwner_IdAndIsDeletedFalse(userId).orElse(null);
        if (userCar != null) {
            // 유저가 등록한 차를 예약한 사용자가 있는 경우
            if (isOnGoing(userCar.getReservations())) {
                throw new GeneralException(ErrorCode.USER_CAR_HAS_RESERVATION);
            }

            // 차의 image 들 delete
            carImageRepository.deleteAll(userCar.getCarImages());
            // 등록한 차량 soft delete
            userCar.setIsDeleted(true);
        }

        // 유저 soft delete
        user.setDeleted(true);
        // 발급받은 리프레시 토큰 삭제
        refreshTokenService.deleteRefreshToken(userId);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User loginUser = userRepository.findFirstByEmailAndIsDeletedFalse(loginRequestDto.getEmail()).orElseThrow(() ->
                new GeneralException(ErrorCode.USER_DELETED));

        if (!BCrypt.checkpw(loginRequestDto.getPassword(), loginUser.getPassword())) {
            throw new AuthException(ErrorCode.USER_WRONG_PASSWORD);
        }

        GetUserInfoResponseDto loginUserInfo = getLoginUserInfo(loginUser);
        GetTokenResponseDto tokenResponseDto = GetTokenResponseDto.builder()
                .accessToken(jwtProvider.createAccessToken(loginUser))
                .refreshToken(jwtProvider.createRefreshToken(loginUser.getId()))
                .build();

        return LoginResponseDto.builder()
                .tokens(tokenResponseDto)
                .loginUserInfo(loginUserInfo)
                .build();
    }

    public GetTokenResponseDto refresh(Long userId) {
        User expiredUser = userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() ->
                new AuthException(ErrorCode.USER_NOT_FOUND));

        return GetTokenResponseDto.builder()
                .accessToken(jwtProvider.createAccessToken(expiredUser))
                .build();
    }

    private GetUserInfoResponseDto getLoginUserInfo(User user) {
        return GetUserInfoResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

    private Boolean isOnGoing(List<Reservation> reservations) {
        return reservations.stream().anyMatch(reservation ->
                reservation.getStatus() == ReservationStatus.READY ||
                        reservation.getStatus() == ReservationStatus.USING);
    }
}
