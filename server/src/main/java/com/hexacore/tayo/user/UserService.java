package com.hexacore.tayo.user;

import com.hexacore.tayo.util.Encryptor;
import com.hexacore.tayo.common.ResponseDto;
import com.hexacore.tayo.common.errors.GeneralException;
import com.hexacore.tayo.image.S3Manager;
import com.hexacore.tayo.user.dto.LoginRequestDto;
import com.hexacore.tayo.user.dto.LoginResponseDto;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Manager s3Manager;

    @Transactional
    public ResponseDto update(Long userId, UpdateUserRequestDto updateRequestDto) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
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

        return ResponseDto.success(HttpStatus.OK);
    }

    public UserInfoResponseDto getUser(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new GeneralException(ErrorCode.USER_NOT_FOUND));

        return getUserInfo(user);
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
}
