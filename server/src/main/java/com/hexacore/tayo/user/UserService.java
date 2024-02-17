package com.hexacore.tayo.user;

import com.hexacore.tayo.car.dto.GetCarResponseDto;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.util.S3Manager;
import com.hexacore.tayo.user.dto.UpdateUserRequestDto;
import com.hexacore.tayo.user.dto.GetUserInfoResponseDto;
import com.hexacore.tayo.user.model.User;
import com.hexacore.tayo.util.Encryptor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Manager s3Manager;

    @Transactional
    public void update(Long userId, UpdateUserRequestDto updateRequestDto) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() ->
                new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 시용자 프로필 이미지 수정시 - s3에서 원래 이미지 삭제 후 새로 업로드
        if (updateRequestDto.getProfileImg() != null && !updateRequestDto.getProfileImg().isEmpty()) {
            s3Manager.deleteImage(user.getProfileImgUrl());
            String newProfileImgUrl = s3Manager.uploadImage(updateRequestDto.getProfileImg());
            user.setProfileImgUrl(newProfileImgUrl);
        }

        // 새로운 비밀번호를 입력한 경우
        if (updateRequestDto.getPassword() != null && !updateRequestDto.getPassword().isEmpty()) {
            user.setPassword(Encryptor.encryptPwd(updateRequestDto.getPassword()));
        }

        // todo null 체크를 해줘야할지 클라이언트 상에서 확인 후 수정
        user.setPhoneNumber(updateRequestDto.getPhoneNumber());
    }

    public GetUserInfoResponseDto getUser(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() ->
                new GeneralException(ErrorCode.USER_NOT_FOUND));

        return getUserInfo(user);
    }

    // TODO: #138 이슈
//    public GetCarResponseDto getUserCar(Long userId) {
//        User user = userRepository.getById(userId);
//        if (user.getCar() == null) {
//            throw new GeneralException(ErrorCode.USER_CAR_NOT_EXISTS);
//        }
//        return GetCarResponseDto.of(user.getCar());
//    }

    private GetUserInfoResponseDto getUserInfo(User user) {
        return GetUserInfoResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }
}
