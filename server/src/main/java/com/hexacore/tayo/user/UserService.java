package com.hexacore.tayo.user;

import com.hexacore.tayo.auth.JwtService;
import com.hexacore.tayo.car.model.CarEntity;
import com.hexacore.tayo.car.model.ImageEntity;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.image.S3Manager;
import com.hexacore.tayo.user.dto.*;
import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.user.model.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final S3Manager s3Manager;

    @Transactional
    public ResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        // todo 회원탈퇴했던 사용자가 다시 회원가입한 경우 고려해보기

        // 이메일 중복 확인
        if(userRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
            throw new GeneralException(ErrorCode.USER_EMAIL_DUPLICATED);
        }

        // 프로필 사진 업로드
        String profileUrl = null;
        if (signUpRequestDto.getProfileImg() != null && !signUpRequestDto.getProfileImg().isEmpty()) {
            profileUrl = s3Manager.uploadImage(signUpRequestDto.getProfileImg());
        }

        UserEntity newUser = UserEntity.builder()
                .email(signUpRequestDto.getEmail())
                .name(signUpRequestDto.getName())
                .password(encryptPwd(signUpRequestDto.getPassword()))
                .phoneNumber(signUpRequestDto.getPhoneNumber())
                .profileImgUrl(profileUrl)
                .build();

        userRepository.save(newUser);
        return ResponseDto.success(HttpStatus.CREATED);
    }

    public UserInfoResponseDto getUser(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new GeneralException(ErrorCode.USER_NOT_FOUND));

        return getUserInfo(user);
    }

    @Transactional
    public ResponseDto update(Long userId, UserUpdateRequestDto updateRequestDto) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 시용자 프로필 이미지 수정시 - s3에서 원래 이미지 삭제 후 새로 업로드
        if (updateRequestDto.getProfileImg()!= null && !updateRequestDto.getProfileImg().isEmpty()) {
            s3Manager.deleteImage(user.getProfileImgUrl());
            String newProfileImgUrl = s3Manager.uploadImage(updateRequestDto.getProfileImg());
            user.setProfileImgUrl(newProfileImgUrl);
        }

        // 새로운 비밀번호를 입력한 경우
        if (updateRequestDto.getPassword()!= null && !updateRequestDto.getPassword().isEmpty()) {
            user.setPassword(encryptPwd(updateRequestDto.getPassword()));
        }

        // todo null 체크를 해줘야할지 클라이언트 상에서 확인 후 수정
        user.setPhoneNumber(updateRequestDto.getPhoneNumber());

        return ResponseDto.success(HttpStatus.OK);
    }

    @Transactional
    public void logOut(Long userId) {
        jwtService.deleteRefreshToken(userId);
    }

    @Transactional
    public void delete(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 유저 soft delete
        user.setDeleted(true);

        // 유저가 등록한 차가 있는 경우, 차도 soft delete
        CarEntity userCar = user.getCar();
        if (userCar != null) {
            userCar.setIsDeleted(true);

            // 차의 image 들 soft delete
            for (ImageEntity carImage : userCar.getImages()) {
                carImage.setIsDeleted(true);
            }
        }

        // 발급받은 리프레시 토큰 삭제
        jwtService.deleteRefreshToken(userId);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        UserEntity loginUser = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() ->
                new AuthException(ErrorCode.USER_NOT_FOUND));

        if (loginUser.isDeleted()) {
            throw new AuthException(ErrorCode.USER_DELETED);
        }

        if (!BCrypt.checkpw(loginRequestDto.getPassword(), loginUser.getPassword())) {
            throw new AuthException(ErrorCode.USER_WRONG_PASSWORD);
        }

        UserInfoResponseDto loginUserInfo = getUserInfo(loginUser);

        return LoginResponseDto.builder()
                .accessToken(jwtService.createAccessToken(loginUser))
                .refreshToken(jwtService.createRefreshToken(loginUser.getId()))
                .loginUserInfo(loginUserInfo)
                .build();
    }

    public String refresh(Long userId) {
        UserEntity expiredUser = userRepository.findById(userId).orElseThrow(() ->
                new AuthException(ErrorCode.USER_NOT_FOUND));

        return jwtService.createAccessToken(expiredUser);
    }

    private UserInfoResponseDto getUserInfo(UserEntity user) {
        return UserInfoResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

    // 비밀번호 암호화
    private String encryptPwd(String plainPwd) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(plainPwd, salt);
    }
}
